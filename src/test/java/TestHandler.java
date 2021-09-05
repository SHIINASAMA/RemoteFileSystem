import pers.kaoru.rfs.core.FileInfo;
import pers.kaoru.rfs.core.web.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;

public class TestHandler extends Thread {

    public static void main(String[] args) throws IOException {
        testListShow();
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(8080);
            Socket client = server.accept();
            ImplHandler handler = new MainHandler();
            handler.handle(client);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void testListShow() throws IOException {
        TestHandler testHandler = new TestHandler();
        testHandler.start();

        Socket socket = new Socket("localhost", 8080);
        Request request = new Request();
        request.setMethod(RequestMethod.LIST_SHOW);
        request.setHeader("source", "/");
        WebUtils.WriteRequest(socket, request);

        Response response = WebUtils.ReadResponse(socket);
        if (response.getCode() == ResponseCode.OK) {
            System.out.println("Client> OK");
            LinkedList<FileInfo> fileInfos = FileInfo.FileInfosBuild(response.getHeader("list"));
            for (FileInfo fileInfo : fileInfos) {
                System.out.println("Client> name: " + fileInfo.getName() +
                        ", isDirectory: " + fileInfo.isDirectory() +
                        ", size: " + fileInfo.getSize() +
                        ", last: " + new Date(fileInfo.getLast()));
            }
        } else {
            System.out.println("Client> " + response.getHeader("error"));
        }
    }
}
