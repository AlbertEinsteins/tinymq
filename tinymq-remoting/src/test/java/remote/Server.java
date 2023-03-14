package remote;

import com.tinymq.remote.netty.NettyRemotingServer;
import com.tinymq.remote.netty.NettyServerConfig;
import com.tinymq.remote.netty.RemotingProcessor;
import com.tinymq.remote.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

public class Server {
    static class MyProcessor implements RemotingProcessor {
        @Override
        public RemotingCommand process(ChannelHandlerContext ctx, RemotingCommand request) {
            String receive = new String(request.getBody(), StandardCharsets.UTF_8);
            System.out.println(receive);

            RemotingCommand response = RemotingCommand.createResponse(request.getCode(), "success");
            response.setBody(request.getBody());
            return response;
        }

        @Override
        public boolean rejectRequest() {
            return false;
        }
    }
    public static void main(String[] args) {
        NettyServerConfig serverConfig = new NettyServerConfig();
        serverConfig.setListenPort(7800);

        NettyRemotingServer server = new NettyRemotingServer(serverConfig);
        server.registerProcessor(100, new MyProcessor(), null);

        server.start();
    }
}
