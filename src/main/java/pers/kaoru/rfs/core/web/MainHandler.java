package pers.kaoru.rfs.core.web;

import org.apache.logging.log4j.core.Logger;
import pers.kaoru.rfs.core.FileInfo;
import pers.kaoru.rfs.core.FileOperator;
import pers.kaoru.rfs.core.ImplFileOperator;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class MainHandler implements ImplHandler {

    private final ImplFileOperator operator;
    private final String prefixPath;
    private final UserManger userManger;
    private final Logger log;

    public static MainHandler HandlerBuild(String prefixPath, LinkedList<UserInfo> users, Logger logger) {
        UserManger userManger = new UserManger();
        for (var user : users) {
            userManger.addUser(user);
        }
        return new MainHandler(prefixPath, userManger, logger);
    }

    private MainHandler(String prefixPath, UserManger userManger, Logger logger) {
        this.prefixPath = prefixPath;
        operator = new FileOperator();
        this.userManger = userManger;
        log = logger;
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
                    verify(socket, request, response);
                    break;
                default:
                    break;
            }
            WebUtils.WriteResponse(socket, response);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.warn(exception.getMessage());
        } finally {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (Exception exception) {
                exception.printStackTrace();
                log.warn(exception.getMessage());
            }
        }
    }

    private boolean checkPath(String path) {
        if (path.contains("..")) {
            log.warn("illegal path");
            return false;
        }
        return true;
    }

    private String checkPermission(Request request, Response response, UserPermission permission) {
        String token = request.getHeader("token");
        if (token == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Request a token");
            log.warn("no token");
            return null;
        }

        var result = WebUtils.VerifyToken(token);
        if (result == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Invalid token");
            log.warn("invalid token");
            return null;
        }

        UserInfo info = userManger.getUser(result.get("username"));
        if (info == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "No user");
            log.warn("no user");
            return null;
        }

        if (!UserManger.VerifyPermission(info, permission)) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Permission denied");
            log.warn("permission denied [" + info.getName() + "]");
            return null;
        }

        return info.getName();
    }

    private void listShow(Socket socket, Request request, Response response) throws IOException {
        String username = checkPermission(request, response, UserPermission.READ);
        if (username == null) {
            return;
        }

        String source = request.getHeader("source");
        if (source == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            log.warn("illegal parameter");
            return;
        }

        if (!checkPath(source)) {
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
            log.info("list show [" + username + "] " + source);
        } else {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal path");
            log.warn("illegal path");
        }
    }

    private void remove(Socket socket, Request request, Response response) throws IOException {
        String username = checkPermission(request, response, UserPermission.BOTH);
        if (username == null) {
            return;
        }

        String source = request.getHeader("source");
        if (source == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            log.warn("illegal parameter");
            return;
        }

        if (!checkPath(source)) {
            return;
        }

        File root = new File(prefixPath);
        File srcFile = new File(prefixPath + source);
        if (root.equals(srcFile)) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            log.warn("illegal parameter");
            return;
        }

        if (srcFile.exists()) {
            if (operator.remove(srcFile)) {
                response.setCode(ResponseCode.OK);
                log.info("remove [" + username + "] " + source);
            } else {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Remove operate fail");
                log.warn("remove operate file: " + source);
            }
        } else {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "File does not exist");
            log.warn("file does not exist: " + source);
        }
    }

    private void copy(Socket socket, Request request, Response response) throws IOException {
        String username = checkPermission(request, response, UserPermission.BOTH);
        if (username == null) {
            return;
        }

        String source = request.getHeader("source");
        String destination = request.getHeader("destination");
        if (source == null || destination == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            log.warn("illegal parameter");
            return;
        }

        if (!checkPath(source) && !checkPath(destination)) {
            return;
        }

        File srcFile = new File(prefixPath + source);
        File destFile = new File(prefixPath + destination);
        if (srcFile.exists() && !destFile.exists()) {
            if (operator.copy(srcFile, destFile)) {
                response.setCode(ResponseCode.OK);
                log.info("copy [" + username + "] " + source + " -> " + destination);
            } else {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Copy operate fail");
            }
        } else {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal path");
            log.warn("illegal path: " + source + " -> " + destination);
        }
    }

    private void move(Socket socket, Request request, Response response) throws IOException {
        String username = checkPermission(request, response, UserPermission.BOTH);
        if (username == null) {
            return;
        }

        String source = request.getHeader("source");
        String destination = request.getHeader("destination");
        if (source == null || destination == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            log.warn("illegal parameter");
            return;
        }

        if (!checkPath(source) && !checkPath(destination)) {
            return;
        }

        File srcFile = new File(prefixPath + source);
        File destFile = new File(prefixPath + destination);
        if (srcFile.exists() && !destFile.exists()) {
            if (operator.move(srcFile, destFile)) {
                response.setCode(ResponseCode.OK);
                log.info("move [" + username + "] " + source + " -> " + destination);
            } else {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Move operate fail");
                log.warn("move operate fail: " + source + " -> " + destination);
            }
        } else {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal path");
            log.warn("illegal path: " + source + " -> " + destination);
        }
    }

    private void makeDirectory(Socket socket, Request request, Response response) throws IOException {
        String username = checkPermission(request, response, UserPermission.BOTH);
        if (username == null) {
            return;
        }

        String source = request.getHeader("source");
        if (source == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            log.warn("illegal parameter");
            return;
        }

        if (!checkPath(source)) {
            return;
        }

        File srcFile = new File(prefixPath + source);
        if (!srcFile.exists()) {
            if (operator.makeDirectory(srcFile)) {
                response.setCode(ResponseCode.OK);
            } else {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Make directory operate fail");
                log.warn("make directory operate fail: " + source);
            }
        } else {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "File already exist");
            log.warn("file already exist: " + source);
        }
    }

    private void upload(Socket socket, Request request, Response response) throws IOException {
        String username = checkPermission(request, response, UserPermission.BOTH);
        if (username == null) {
            return;
        }

        String source = request.getHeader("source");
        String rangeStr = request.getHeader("range");
        if (source == null || rangeStr == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            log.warn("illegal parameter");
            return;
        }

        if (!checkPath(source)) {
            return;
        }

        File srcFile = new File(prefixPath + source);
        if (srcFile.exists()) {
            if (srcFile.isDirectory()) {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Illegal path");
                log.warn("illegal path");
                return;
            }
        } else {
            if (!srcFile.createNewFile()) {
                response.setCode(ResponseCode.FAIL);
                response.setHeader("error", "Create file fail");
                log.warn("create file fail: " + source);
                return;
            }
        }

        Range range = Range.RangeBuild(rangeStr);
        if (range.getBegin() > srcFile.length()) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal seek location");
            log.warn("illegal seek location: " + range.getBegin());
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
            log.info("upload [" + username + "] " + source);
        }
    }

    private void download(Socket socket, Request request, Response response) throws IOException {
        String username = checkPermission(request, response, UserPermission.READ);
        if (username == null) {
            return;
        }

        String source = request.getHeader("source");
        String rangeStr = request.getHeader("range");

        if (source == null || rangeStr == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            log.warn("illegal parameter");
            return;
        }

        if (!checkPath(source)) {
            return;
        }

        File srcFile = new File(prefixPath + source);
        if (!srcFile.exists()) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal path");
            log.warn("illegal path");
            return;
        }

        Range range = Range.RangeBuild(rangeStr);
        if (range.getBegin() > srcFile.length()) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal seek location");
            log.warn("illegal seek location: " + range.getBegin());
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
            log.info("download [" + username + "] " + source + "[" + range.getBegin() + "-" + range.getEnd() + "]");
        }
    }

    private void verify(Socket socket, Request request, Response response) throws IOException {
        String name = request.getHeader("username");
        String pwd = request.getHeader("password");
        if (name == null || pwd == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "Illegal parameter");
            log.warn("illegal parameter");
            return;
        }

        UserInfo info = userManger.getUser(name);
        if (info == null) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "No user [" + name + "]");
            log.warn("no user");
            return;
        }

        if (!info.getPassword().equals(pwd)) {
            response.setCode(ResponseCode.FAIL);
            response.setHeader("error", "wrong password");
            log.warn("wrong password [" + name + "]");
            return;
        }

        String token = WebUtils.MakeToken(name, pwd);
        response.setCode(ResponseCode.OK);
        response.setHeader("token", token);
        log.info("make token [" + name + "]");
    }
}
