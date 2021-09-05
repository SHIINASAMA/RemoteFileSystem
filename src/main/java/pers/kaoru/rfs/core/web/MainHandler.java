package pers.kaoru.rfs.core.web;

import pers.kaoru.rfs.core.FileInfo;
import pers.kaoru.rfs.core.FileOperator;
import pers.kaoru.rfs.core.ImplFileOperator;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class MainHandler implements ImplHandler {

    private final ImplFileOperator operator;

    public MainHandler() {
        operator = new FileOperator();
    }

    @Override
    public void handle(Socket socket) {
        try {
            Request request = WebUtils.ReadRequest(socket);
            switch (request.getMethod()) {
                case LIST_SHOW:
                    listShow(socket, request);
                    break;
                case REMOVE:
                    break;
                case COPY:
                    break;
                case MOVE:
                    break;
                case UPLOAD:
                    break;
                case DOWNLOAD:
                    break;
                case VERIFY:
                    break;
                default:
                    break;
            }
        }catch (IOException exception){
            exception.printStackTrace();
        }finally {
            try{
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            }catch (IOException exception){
                exception.printStackTrace();
            }
        }
    }

    private void listShow(Socket socket, Request request) throws IOException {
        Response response = new Response();

        String source = request.getHeader("source");
        if (source == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            WebUtils.WriteResponse(socket, response);
            return;
        }

        File srcFile = new File(source);
        if (srcFile.exists() && srcFile.isDirectory()) {
            File[] files = operator.listShow(srcFile);
            LinkedList<FileInfo> list = new LinkedList<>();
            for (File file : files) {
                list.add(new FileInfo(file));
            }
            String str = list.toString();
            response.setCode(ResponseCode.OK);
            response.setHeader("list", str);
        } else {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal path");
        }
        WebUtils.WriteResponse(socket, response);
    }
}
