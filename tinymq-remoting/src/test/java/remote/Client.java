package remote;



import com.tinymq.remote.exception.RemotingConnectException;
import com.tinymq.remote.exception.RemotingSendRequestException;
import com.tinymq.remote.exception.RemotingTimeoutException;
import com.tinymq.remote.netty.NettyClientConfig;
import com.tinymq.remote.netty.NettyRemotingClient;
import com.tinymq.remote.protocol.RemotingCommand;

import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) throws RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, InterruptedException {
        NettyClientConfig clientConfig = new NettyClientConfig();
        NettyRemotingClient client = new NettyRemotingClient(clientConfig);


        client.start();

        RemotingCommand msg = new RemotingCommand();
        String body = "123";
        msg.setBody(body.getBytes(StandardCharsets.UTF_8));

        // set request code
        msg.setCode(100);

//        final InvokeCallback callback = new InvokeCallback() {
//            @Override
//            public void operationComplete(ResponseFuture responseFuture) {
//                RemotingMessage message = null;
//                try {
//                    message = responseFuture.waitResponse();
//                } catch (InterruptedException e) {
//                    System.out.println(e);
//                    throw new RuntimeException(e);
//                }
//                System.out.println(message);
//            }
//        };

        client.invokeOneway("127.0.0.1:7800", msg, 10000);
    }
}
