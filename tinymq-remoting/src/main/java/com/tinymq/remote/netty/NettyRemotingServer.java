package com.tinymq.remote.netty;

import cn.hutool.core.lang.Pair;
import com.tinymq.remote.InvokeCallback;
import com.tinymq.remote.RPCHook;
import com.tinymq.remote.RemotingServer;
import com.tinymq.remote.exception.RemotingSendRequestException;
import com.tinymq.remote.exception.RemotingTimeoutException;
import com.tinymq.remote.protocol.RemotingCommand;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyRemotingServer extends AbstractNettyRemoting
    implements RemotingServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRemotingServer.class);

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    private final EventLoopGroup boss;
    private final EventLoopGroup eventLoopGroupWorker;

    private final NettyServerConfig nettyServerConfig;

    private final ExecutorService publicExecutor;

    private final NettyEventListener eventListener;

    private final Timer timer = new Timer("cleanServerResponseTableTimer", true);

    private DefaultEventLoopGroup defaultEventLoopGroup;


    private int port;

    /* prepare sharable handler */
    private NettyEncoder encoder;

    private NettyServerHandler nettyServerHandler;

    public NettyRemotingServer(NettyServerConfig nettyServerConfig) {
        this(nettyServerConfig, null);
    }
    public NettyRemotingServer(NettyServerConfig nettyServerConfig, NettyEventListener nettyEventListener) {
        super(nettyServerConfig.getServerOnewaySemaphoreValue(), nettyServerConfig.getServerAsyncSemaphoreValue());
        this.nettyServerConfig = nettyServerConfig;
        this.eventListener = nettyEventListener;

        int publicNums = nettyServerConfig.getServerCallbackExecutorThreads();
        if(publicNums <= 0) {
            publicNums = 4;
        }

        this.publicExecutor = Executors.newFixedThreadPool(publicNums, new ThreadFactory() {
            private final AtomicInteger threadIdx = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ServerPublicExecutorThread_" + threadIdx.getAndIncrement());
            }
        });
        this.boss = new NioEventLoopGroup(1, new ThreadFactory() {
            private final AtomicInteger threadIdx = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ServerBossEventLoopGroupThread_" + threadIdx.getAndIncrement());
            }
        });

        this.eventLoopGroupWorker = new NioEventLoopGroup(nettyServerConfig.getServerWorkerThreads(), new ThreadFactory() {
            private final AtomicInteger threadIdx = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ServerWorkerEventLoopGroupThread_" + threadIdx.getAndIncrement());
            }
        });

        // 清理请求响应表
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                scanResponseTable();
            }
        }, 3 * 1000, 1000);
    }

    public void start() {
        this.defaultEventLoopGroup = new DefaultEventLoopGroup(nettyServerConfig.getServerWorkerThreads(), new ThreadFactory() {
            private final AtomicInteger threadIdx = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ServerDefaultEventLoopGroupThread_" + threadIdx.getAndIncrement());
            }
        });

        prepareSharableHandler();

        this.serverBootstrap.group(boss, eventLoopGroupWorker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
                .localAddress(nettyServerConfig.getListenPort())
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(defaultEventLoopGroup,
                                encoder,
                                new NettyDecoder(),
                                new NettyServerHandler());
                    }
                });

        try {
            ChannelFuture sync = serverBootstrap.bind().sync();
            InetSocketAddress address = (InetSocketAddress) sync.channel().localAddress();
            this.port = address.getPort();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void prepareSharableHandler() {
        this.encoder = new NettyEncoder();
        this.nettyServerHandler = new NettyServerHandler();
    }

    public void shutdown() {
        this.timer.cancel();
        // shutdown executors
        try {
            this.boss.shutdownGracefully();
            this.eventLoopGroupWorker.shutdownGracefully();

            this.defaultEventLoopGroup.shutdownGracefully();
            this.publicExecutor.shutdown();

        } catch (Exception e) {
            LOGGER.warn("server shutdown exception", e);
        }
    }

    @Override
    public void registerProcessor(int requestCode, RemotingProcessor processor, ExecutorService executor) {
        ExecutorService curExecutor = executor;
        if(curExecutor == null) {
            curExecutor = this.publicExecutor;
        }
        Pair<RemotingProcessor, ExecutorService> servicePair = new Pair<>(processor, curExecutor);
        this.processorTable.put(requestCode, servicePair);
    }

    @Override
    public int localListenPort() {
        return this.port;
    }

    @Override
    public void registerDefaultProcessor(RemotingProcessor processor, ExecutorService executor) {
        this.defaultProcessor = new Pair<>(processor, executor);
    }

    @Override
    public RemotingCommand invokeSync(Channel channel, RemotingCommand request, long timeoutMillis)
            throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException {
        return this.invokeSyncImpl(channel, request, timeoutMillis);
    }

    @Override
    public void invokeAsync(Channel channel, RemotingCommand request, long timeoutMillis, InvokeCallback invokeCallback) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException {
        this.invokeAsyncImpl(channel, request, timeoutMillis, invokeCallback);
    }

    @Override
    public void invokeOneway(Channel channel, RemotingCommand request, long timeoutMillis) throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException {
        this.invokeOnewayImpl(channel, request, timeoutMillis);
    }

    @Override
    public void registerRPCHook(RPCHook hook) {
        if(hook != null && !rpcHookList.contains(hook)) {
            rpcHookList.add(hook);
        }
    }


    @Override
    public ExecutorService getCallbackExecutor() {
        return this.publicExecutor;
    }

    @ChannelHandler.Sharable
    class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
            processMessage(ctx, msg);
        }
    }
}
