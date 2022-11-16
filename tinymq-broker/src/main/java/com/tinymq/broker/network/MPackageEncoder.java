package com.tinymq.broker.network;

import com.tinymq.broker.protocal.MPackage;
import com.tinymq.broker.protocal.ProtocalBuilder;
import com.tinymq.broker.protocal.Request;
import com.tinymq.broker.protocal.enumerate.SerializeType;
import com.tinymq.broker.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * 将request封装为MPackage
 */
public class MPackageEncoder extends ChannelOutboundHandlerAdapter {
    private Serializer serializer;
    private SerializeType serializeType;
    public MPackageEncoder(Serializer serializer, SerializeType serializeType) {
        this.serializer = serializer;
        this.serializeType = serializeType;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof Request) {
            Request req = (Request) msg;
            byte[] body = serializer.serialize(req.getObject());
            MPackage mPackage = MPackage.build(req.getPackageType(), serializeType, body);
            ByteBuf buf = ctx.alloc().buffer();
            byte[] mPackageBytes = new ProtocalBuilder().encodePackage(mPackage);
            buf.writeBytes(mPackageBytes);
            ctx.writeAndFlush(buf, promise);
        }
        else {
            throw new RuntimeException(String.format("The request type %s is rejected", msg.getClass()));
        }
    }

}
