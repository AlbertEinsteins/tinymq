package com.tinymq.broker.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class BrokerServer {

    public void start() {
        start0();
    }

    private void start0() {
        EventLoopGroup main = new NioEventLoopGroup(1);
        EventLoopGroup sub = new NioEventLoopGroup(5);
        try {
            ServerBootstrap server = new ServerBootstrap()
                    .group(main, sub)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new BrokerServerInitializer());

            ChannelFuture channelFuture = server.bind("127.0.0.1", 9000).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            main.shutdownGracefully();
            sub.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        BrokerServer server = new BrokerServer();
        server.start();
    }
}
