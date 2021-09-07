package pers.kaoru.rfs.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import pers.kaoru.rfs.core.web.UserInfo;
import pers.kaoru.rfs.core.web.UserPermission;

import java.io.*;
import java.util.LinkedList;

public class Config {

    private final String host;
    private final int port;
    private final String workDirectory;
    private final LinkedList<UserInfo> users;

    public static Config ConfigBuild(String path) throws IOException {
        String host;
        int port;
        String wd;
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

        return new Config(host, port, wd, users);
    }

    private Config(String host, int port, String workDirectory, LinkedList<UserInfo> users) {
        this.host = host;
        this.port = port;
        this.workDirectory = workDirectory;
        this.users = users;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getWorkDirectory() {
        return workDirectory;
    }

    public LinkedList<UserInfo> getUsers() {
        return users;
    }
}
