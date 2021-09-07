import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import pers.kaoru.rfs.core.FileInfo;
import pers.kaoru.rfs.core.web.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;

/**
 * @deprecated MainHandler 添加权限控制，不再适用
 */
public class TestHandler extends Thread {

    public static void main(String[] args) throws IOException {
//        testListShow();
//        testRemove();
//        testCopy();
//        testMove();
//        testMakeDirectory();
//        testUpload();
//        testDownload();
        testVerify();
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(8080);
            ImplHandler handler = new MainHandler("E:/", (Logger) LogManager.getLogger(LogManager.ROOT_LOGGER_NAME));
            Socket client = server.accept();
            handler.handle(client);
            // 测试 Download 时需要
//            client = server.accept();
//            handler.handle(client);

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
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }

    private static void testRemove() throws IOException {
        TestHandler testHandler = new TestHandler();
        testHandler.start();

        Socket socket = new Socket("localhost", 8080);

        Request request = new Request();
        request.setMethod(RequestMethod.REMOVE);
        request.setHeader("source", "/");
        WebUtils.WriteRequest(socket, request);

        Response response = WebUtils.ReadResponse(socket);
        System.out.println("Client> " + response.getCode());
        if (response.getCode() == ResponseCode.FAIL) {
            System.out.println("Client> " + response.getHeader("error"));
        }

        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }

    private static void testCopy() throws IOException {
        TestHandler testHandler = new TestHandler();
        testHandler.start();

        Socket socket = new Socket("localhost", 8080);

        Request request = new Request();
        request.setMethod(RequestMethod.COPY);
        request.setHeader("source", "/test0");
        request.setHeader("destination", "/test1");
        WebUtils.WriteRequest(socket, request);

        Response response = WebUtils.ReadResponse(socket);
        System.out.println("Client> " + response.getCode());
        if (response.getCode() == ResponseCode.FAIL) {
            System.out.println("Client> " + response.getHeader("error"));
        }

        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }

    private static void testMove() throws IOException {
        TestHandler testHandler = new TestHandler();
        testHandler.start();

        Socket socket = new Socket("localhost", 8080);

        Request request = new Request();
        request.setMethod(RequestMethod.MOVE);
        request.setHeader("source", "/test0");
        request.setHeader("destination", "/test1");
        WebUtils.WriteRequest(socket, request);

        Response response = WebUtils.ReadResponse(socket);
        System.out.println("Client> " + response.getCode());
        if (response.getCode() == ResponseCode.FAIL) {
            System.out.println("Client> " + response.getHeader("error"));
        }

        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }

    private static void testMakeDirectory() throws IOException {
        TestHandler testHandler = new TestHandler();
        testHandler.start();

        Socket socket = new Socket("localhost", 8080);

        Request request = new Request();
        request.setMethod(RequestMethod.MAKE_DIRECTORY);
        request.setHeader("source", "/test0");
        WebUtils.WriteRequest(socket, request);

        Response response = WebUtils.ReadResponse(socket);
        System.out.println("Client> " + response.getCode());
        if (response.getCode() == ResponseCode.FAIL) {
            System.out.println("Client> " + response.getHeader("error"));
        }

        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }

    private static void testUpload() throws IOException {
        TestHandler testHandler = new TestHandler();
        testHandler.start();

        Socket socket = new Socket("localhost", 8080);

        File file = new File("D:/aria2/aria2.conf");
        Range range = new Range(0L, file.length() - 1, file.length());
        Request request = new Request();
        request.setMethod(RequestMethod.UPLOAD);
        request.setHeader("source", "/aria2.conf");
        request.setHeader("range", range.toString());
        WebUtils.WriteRequest(socket, request);

        var webStream = socket.getOutputStream();
        var localStream = new FileInputStream(file);

        byte[] bytes = new byte[1024];
        int count;
        try {
            while ((count = localStream.read(bytes)) > 0) {
                webStream.write(bytes, 0, count);
            }
        } finally {
            Response response = WebUtils.ReadResponse(socket);
            System.out.println("Client> " + response.getCode());
            if (response.getCode() == ResponseCode.FAIL) {
                System.out.println("Client> " + response.getHeader("error"));
            }

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        }
    }

    private static void testDownload() throws IOException {
        TestHandler testHandler = new TestHandler();
        testHandler.start();

        Socket socket = new Socket("localhost", 8080);
        Range range = new Range(0L, 0L, 0L);
        Request request = new Request();
        request.setMethod(RequestMethod.DOWNLOAD);
        request.setHeader("source", "/test");
        request.setHeader("range", range.toString());
        WebUtils.WriteRequest(socket, request);
        Response response = WebUtils.ReadResponse(socket);
        String rangeStr = response.getHeader("range");
        assert rangeStr != null;
        range = Range.RangeBuild(rangeStr);
        range = new Range(0L, range.getTotal() - 1, range.getTotal());
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();

        socket = new Socket("localhost", 8080);
        request.setHeader("range", range.toString());
        WebUtils.WriteRequest(socket, request);

        var webStream = socket.getInputStream();
        var localStream = new FileOutputStream("D:/test");

        byte[] bytes = new byte[1024];
        long count = range.getTotal();
        try {
            while (count > 0) {
                long current;
                if (count > 1024) {
                    current = 1024;
                } else {
                    current = count;
                }
                long length = webStream.read(bytes, 0, (int) current);
                if (length == -1) {
                    break;
                }
                count -= length;
                localStream.write(bytes, 0, (int) length);
            }

        } finally {
            response = WebUtils.ReadResponse(socket);
            System.out.println("Client> " + response.getCode());
            if (response.getCode() == ResponseCode.FAIL) {
                System.out.println("Client> " + response.getHeader("error"));
            }

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        }
    }

    private static void testVerify() throws IOException {

        TestHandler testHandler = new TestHandler();
        testHandler.start();

        Socket socket = new Socket("localhost", 8080);

        Request request = new Request();
        request.setMethod(RequestMethod.VERIFY);
        request.setHeader("username", "kaoru");
        request.setHeader("password", "123");

        WebUtils.WriteRequest(socket, request);
        Response response = WebUtils.ReadResponse(socket);

        System.out.println("Client> " + response.getCode());
        for (var kv : response.getHeaders().entrySet()) {
            System.out.println("Client> " + kv.getKey() + ": " + kv.getValue());
        }
    }
}
