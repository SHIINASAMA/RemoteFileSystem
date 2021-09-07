import pers.kaoru.rfs.core.web.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestWebUtils extends Thread {
    public static void main(String[] args) throws IOException {
        test();
        testToken();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            var client = serverSocket.accept();

            Request request = WebUtils.ReadRequest(client);
            System.out.println("Server> " + request.getMethod());
            for (var header : request.getHeaders().entrySet()) {
                System.out.println("Server> " + header.getKey() + ": " + header.getValue());
            }

            Response response = new Response();
            response.setCode(ResponseCode.OK);
            response.setHeader("msg", "hi");
            WebUtils.WriteResponse(client, response);

            client.shutdownOutput();
            client.shutdownInput();
            client.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void test() throws IOException {
        TestWebUtils testWebUtils = new TestWebUtils();
        testWebUtils.start();

        Socket socket = new Socket("localhost", 8080);

        Request request = new Request();
        request.setMethod(RequestMethod.LIST_SHOW);
        request.setHeader("msg", "hello");
        WebUtils.WriteRequest(socket, request);

        Response response = WebUtils.ReadResponse(socket);
        System.out.println("Client> " + response.getCode());
        for (var headers : response.getHeaders().entrySet()) {
            System.out.println("Client> " + headers.getKey() + ": " + headers.getValue());
        }

        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }

    private static void testToken() {
        String token = WebUtils.MakeToken("kaoru", "123");
        var headers = WebUtils.VerifyToken(token);
        if (headers != null) {
            for(var kv : headers.entrySet()){
                System.out.println(kv.getKey() + ": " + kv.getValue());
            }
        }
    }
}
