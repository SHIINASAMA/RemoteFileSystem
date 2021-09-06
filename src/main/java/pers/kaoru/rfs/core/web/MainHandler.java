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
    private final String prefixPath;

    public MainHandler(String prefixPath) {
        this.prefixPath = prefixPath;
        operator = new FileOperator();
    }

    @Override
    public void handle(Socket socket) {
        try {
            Request request = WebUtils.ReadRequest(socket);
            Response response = new Response();
            switch (request.getMethod()) {
                case LIST_SHOW:
                    listShow(socket, request, response);
                    break;
                case REMOVE:
                    remove(socket, request, response);
                    break;
                case COPY:
                    copy(socket, request, response);
                    break;
                case MOVE:
                    Move(socket, request, response);
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
            WebUtils.WriteResponse(socket, response);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void listShow(Socket socket, Request request, Response response) throws IOException {
        String source = request.getHeader("source");
        if (source == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            return;
        }

        File srcFile = new File(prefixPath + source);
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
    }

    private void remove(Socket socket, Request request, Response response) throws IOException {
        String source = request.getHeader("source");
        if (source == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            return;
        }

        File root = new File(prefixPath);
        File srcFile = new File(prefixPath + source);
        if (root.equals(srcFile)) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            return;
        }

        if (srcFile.exists()) {
            if (operator.remove(srcFile)) {
                response.setCode(ResponseCode.OK);
            } else {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Remove operate fail");
            }
        } else {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "File does not exist");
        }
    }

    private void copy(Socket socket, Request request, Response response) throws IOException {
        String source = request.getHeader("source");
        String destination = request.getHeader("destination");
        if (source == null || destination == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            return;
        }

        File srcFile = new File(prefixPath + source);
        File destFile = new File(prefixPath + destination);
        if (srcFile.exists() && !destFile.exists()) {
            if (operator.copy(srcFile, destFile)) {
                response.setCode(ResponseCode.OK);
            } else {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Copy operate fail");
            }
        } else {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal path");
        }
    }

    private void Move(Socket socket, Request request, Response response) throws IOException {
        String source = request.getHeader("source");
        String destination = request.getHeader("destination");
        if (source == null || destination == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            return;
        }

        File srcFile = new File(prefixPath + source);
        File destFile = new File(prefixPath + destination);
        if (srcFile.exists() && !destFile.exists()) {
            if (operator.move(srcFile, destFile)) {
                response.setCode(ResponseCode.OK);
            } else {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Move operate fail");
            }
        } else {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal path");
        }
    }
}
