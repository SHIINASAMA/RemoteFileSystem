package pers.kaoru.rfs.client;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Config {
    private String downloadDir;
    private String lastHost;
    private int lastPort;
    private String lastName;

    public static Config ConfigBuilder(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
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
        var downloadDir = jsonObject.getString("download");
        if (downloadDir == null) downloadDir = "/Downloads";

        var lastHost = jsonObject.getString("lastHost");
        if (lastHost == null) lastHost = "";
        var lastPort = jsonObject.getIntValue("lastPort");
        var lastName = jsonObject.getString("lastName");
        if (lastName == null) lastName = "";

        return new Config(lastHost, lastPort, lastName, downloadDir);
    }

    public static void ConfigStore(Config config) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("download", config.getDownloadDir());
        jsonObject.put("lastHost", config.getLastHost());
        jsonObject.put("lastPort", config.getLastPort());
        jsonObject.put("lastName", config.getLastName());
        String rawData = jsonObject.toJSONString();

        try {
            try (OutputStream outputStream = new FileOutputStream("client.json")) {
                outputStream.write(rawData.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Config(String lastHost, int lastPort, String lastName, String downloadDir) {
        this.lastHost = lastHost;
        this.lastPort = lastPort;
        this.lastName = lastName;
        this.downloadDir = downloadDir;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }

    public String getLastHost() {
        return lastHost;
    }

    public void setLastHost(String lastHost) {
        this.lastHost = lastHost;
    }

    public int getLastPort() {
        return lastPort;
    }

    public void setLastPort(int lastPort) {
        this.lastPort = lastPort;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
