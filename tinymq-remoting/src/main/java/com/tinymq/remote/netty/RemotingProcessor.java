package com.tinymq.remote.netty;

import com.tinymq.remote.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

public interface RemotingProcessor {
    RemotingCommand process(ChannelHandlerContext ctx, RemotingCommand request);

    boolean rejectRequest();
}
