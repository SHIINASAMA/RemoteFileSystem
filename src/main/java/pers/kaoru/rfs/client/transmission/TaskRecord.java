package pers.kaoru.rfs.client.transmission;

import pers.kaoru.rfs.core.MD5Utils;

import java.io.Serializable;
import java.nio.file.Path;

public class TaskRecord implements Serializable {

    private final String host;
    private transient int port;
    private transient String token;

    private long current;
    private long length;

    private final String remoteUrl;
    private final String localUrl;
    private final String name;
    private final TaskType type;

    private final long createTime;
    private String uid;

    public TaskRecord(String host, int port, String token, String remoteUrl, String localUrl, long length, TaskType type) {
        this.host = host;
        this.port = port;
        this.token = token;
        this.length = length;
        this.remoteUrl = remoteUrl;
        this.localUrl = localUrl;
        this.type = type;
        createTime = System.currentTimeMillis();
        if (type == TaskType.DOWNLOAD) {
            this.name = Path.of(remoteUrl).getFileName().toString();
        } else {
            this.name = Path.of(localUrl).getFileName().toString();
        }
    }

    public String getUid() {
        if (uid == null) {
            uid = MD5Utils.GenerateMD5(createTime + type.name());
        }
        return uid;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getToken() {
        return token;
    }

    public long getCurrent() {
        return current;
    }

    public long getLength() {
        return length;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public String getName() {
        return name;
    }

    public TaskType getType() {
        return type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public void setLength(long length){
        this.length = length;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
