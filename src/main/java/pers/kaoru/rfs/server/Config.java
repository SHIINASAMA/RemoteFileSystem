package pers.kaoru.rfs.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import pers.kaoru.rfs.core.web.UserInfo;
import pers.kaoru.rfs.core.web.UserPermission;

import java.io.*;
import java.util.LinkedList;
import java.util.function.Predicate;

public class Config {

    private final String host;
    private final int port;
    private final int backlog;
    private final String workDirectory;
    private final int threads;
    private final LinkedList<UserInfo> users;

    public static Config ConfigBuild(String path) throws IOException {
        String host;
        int port;
        int backlog;
        String wd;
        int threads;
        LinkedList<UserInfo> users = new LinkedList<>();

        File file = new File(path);
        if (!file.exists() && !file.isFile()) {
            throw new FileNotFoundException("\"" + path + "'\"" + "not found");
        }

        InputStream inputStream = new FileInputStream(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int length;
        byte[] bytes = new byte[1024];
        while ((length = inputStream.read(bytes, 0, 1024)) > 0) {
            outputStream.write(bytes, 0, length);
        }

        inputStream.close();
        outputStream.close();
        String rawData = outputStream.toString();

        JSONObject jsonObject = JSONObject.parseObject(rawData);

        host = jsonObject.getString("host");
        if (host == null) {
            host = "localhost";
        }

        port = jsonObject.getIntValue("port");
        if (port == 0) {
            port = 8080;
        }

        backlog = jsonObject.getIntValue("backlog");

        threads = jsonObject.getIntValue("threads");
        if (threads <= 0) {
            threads = 4;
        }

        wd = jsonObject.getString("workdirectory");
        if (wd == null) {
            wd = "/";
        }

        JSONArray jsonArray = jsonObject.getJSONArray("users");
        if (jsonArray == null) {
            users.push(new UserInfo("root", "root", UserPermission.BOTH));
        } else {
            for (Object o : jsonArray) {
                JSONObject next = (JSONObject) o;
                String name = next.getString("username");
                String pwd = next.getString("password");
                String permissionStr = next.getString("permission");
                if (name != null || pwd != null || permissionStr != null) {
                    UserPermission permission = UserPermission.READ;
                    if (permissionStr.equals("rw")) {
                        permission = UserPermission.BOTH;
                    }
                    users.push(new UserInfo(name, pwd, permission));
                }
            }
        }

        return new Config(host, port, backlog, wd, threads, users);
    }

    public Config(String host, int port, int backlog, String workDirectory, int threads, LinkedList<UserInfo> users) {
        this.host = host;
        this.port = port;
        this.backlog = backlog;
        this.workDirectory = workDirectory;
        this.threads = threads;
        this.users = users;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getBacklog() {
        return backlog;
    }

    public String getWorkDirectory() {
        return workDirectory;
    }

    public int getThreads() {
        return threads;
    }

    public LinkedList<UserInfo> getUsers() {
        return users;
    }
}
