package com.tinymq.broker.network;

import com.tinymq.broker.protocal.enumerate.SerializeType;
import com.tinymq.broker.serializer.JSONSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class BrokerServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new MPackageDecoder(new JSONSerializer(), SerializeType.JSON_SERIALIZE));

        pipeline.addLast(new MPackageEncoder(new JSONSerializer(), SerializeType.JSON_SERIALIZE));

    }
}
