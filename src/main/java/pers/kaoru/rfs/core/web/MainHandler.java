package pers.kaoru.rfs.core.web;

import pers.kaoru.rfs.core.FileInfo;
import pers.kaoru.rfs.core.FileOperator;
import pers.kaoru.rfs.core.ImplFileOperator;

import java.io.*;
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
                    move(socket, request, response);
                    break;
                case MAKE_DIRECTORY:
                    makeDirectory(socket, request, response);
                    break;
                case UPLOAD:
                    upload(socket, request, response);
                    break;
                case DOWNLOAD:
                    download(socket, request, response);
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

    private void move(Socket socket, Request request, Response response) throws IOException {
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

    private void makeDirectory(Socket socket, Request request, Response response) throws IOException {
        String source = request.getHeader("source");
        if (source == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            return;
        }

        File srcFile = new File(prefixPath + source);
        if (!srcFile.exists()) {
            if (operator.makeDirectory(srcFile)) {
                response.setCode(ResponseCode.OK);
            } else {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Make directory operate fail");
            }
        } else {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "File already exist");
        }
    }

    private void upload(Socket socket, Request request, Response response) throws IOException {
        String source = request.getHeader("source");
        String rangeStr = request.getHeader("range");
        if (source == null || rangeStr == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            return;
        }

        File srcFile = new File(prefixPath + source);
        if (srcFile.exists()) {
            if (srcFile.isDirectory()) {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Illegal path");
                return;
            }
        } else {
            if (!srcFile.createNewFile()) {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Create file fail");
                return;
            }
        }

        Range range = Range.RangeBuild(rangeStr);
        if (range.getBegin() > srcFile.length()) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal seek location");
            return;
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(srcFile.getPath(), "rw")) {
            randomAccessFile.seek(range.getBegin());
            randomAccessFile.setLength(range.getBegin());
            var inputStream = socket.getInputStream();
            long count = range.getEnd() - range.getBegin() + 1;
            byte[] bytes = new byte[1024];
            while (count > 0) {
                long current;
                if (count > 1024) {
                    current = 1024;
                } else {
                    current = count;
                }
                long length = inputStream.read(bytes, 0, (int) current);
                // 暂时简单处理
                if (length == -1) {
                    return;
                }
                count -= length;
                randomAccessFile.write(bytes, 0, (int) length);
            }
            response.setCode(ResponseCode.OK);
        }
    }

    private void download(Socket socket, Request request, Response response) throws IOException {
        String source = request.getHeader("source");
        String rangeStr = request.getHeader("range");

        if (source == null || rangeStr == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            return;
        }

        File srcFile = new File(prefixPath + source);
        if (!srcFile.exists()) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal path");
            return;
        }

        Range range = Range.RangeBuild(rangeStr);
        if (range.getBegin() > srcFile.length()) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal seek location");
            return;
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(srcFile.getPath(), "r")) {
            randomAccessFile.seek(range.getBegin());
            var outputStream = socket.getOutputStream();
            long count = range.getEnd() - range.getBegin() + 1;
            byte[] bytes = new byte[1024];
            while (count > 0) {
                long current;
                if (count > 1024) {
                    current = 1024;
                } else {
                    current = count;
                }
                int length = randomAccessFile.read(bytes, 0, (int) current);
                if (length == -1) {
                    return;
                }
                count -= length;
                outputStream.write(bytes, 0, length);
            }
            response.setCode(ResponseCode.OK);
            response.setHeader("range", new Range(range.getBegin(), range.getEnd(), srcFile.length()).toString());
        }
    }
}
