package com.tinymq.broker.network;

import com.tinymq.broker.protocal.MPackage;
import com.tinymq.broker.protocal.ProtocalBuilder;
import com.tinymq.broker.protocal.enumerate.SerializeType;
import com.tinymq.broker.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * 将消息封装为MPackage报文,交给下一个的handler处理
 */
public class MPackageDecoder extends ChannelInboundHandlerAdapter {
    private Serializer serializer;
    private SerializeType serializeType;
    public MPackageDecoder(Serializer serializer, SerializeType serializeType) {
        this.serializer = serializer;
        this.serializeType = serializeType;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] buffer = new byte[byteBuf.readableBytes()];
        MPackage mPackage = new ProtocalBuilder().decodePackage(buffer);
        ctx.fireChannelRead(mPackage);
    }
}
