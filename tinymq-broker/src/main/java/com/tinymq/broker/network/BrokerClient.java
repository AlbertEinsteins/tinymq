package com.tinymq.broker.network;

import com.tinymq.broker.protocal.Request;
import com.tinymq.broker.protocal.enumerate.PackageType;
import com.tinymq.broker.protocal.enumerate.SerializeType;
import com.tinymq.broker.serializer.JSONSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class BrokerClient {
    public static void main(String[] args) {

        EventLoopGroup main = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap()
                .group(main)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MPackageDecoder(new JSONSerializer(), SerializeType.JSON_SERIALIZE));

                        pipeline.addLast(new MPackageEncoder(new JSONSerializer(), SerializeType.JSON_SERIALIZE));
                    }
                });
        try {
            ChannelFuture sync = client.connect("127.0.0.1", 9000).sync();
            Channel channel = sync.channel();

            Request request = new Request(PackageType.MESSAGE_REQUEST, "111");
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            main.shutdownGracefully();
        }
    }
}
