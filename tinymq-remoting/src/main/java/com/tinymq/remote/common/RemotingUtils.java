package com.tinymq.remote.common;


import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class RemotingUtils {
    private RemotingUtils() { }

    public static String parseRemoteAddress(final Channel channel) {
        String remoteAddress = channel.remoteAddress().toString();
        return remoteAddress;
    }


    public static InetSocketAddress addrToNetAddress(final String addr) {
        if(addr == null || addr.split(":").length != 2) {
            return null;
        }
        String ip = addr.split(":")[0];
        int port = Integer.parseInt(addr.split(":")[1]);
        return InetSocketAddress.createUnresolved(ip, port);
    }
}
