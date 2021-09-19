package pers.kaoru.rfs.client.transmission;

import pers.kaoru.rfs.core.MD5Utils;

import java.io.Serializable;
import java.nio.file.Path;

public class TaskRecord implements Serializable {

    private final String host;
    private transient int port;

    private long current;
    private final long length;

    private final String remoteUrl;
    private final String name;
    private final TaskType type;

    private final long createTime;
    private String uid;

    public TaskRecord(String host, int port, String remoteUrl, long length, TaskType type) {
        this.host = host;
        this.port = port;
        this.length = length;
        this.remoteUrl = remoteUrl;
        this.type = type;
        createTime = System.currentTimeMillis();
        this.name = Path.of(remoteUrl).getFileName().toString();
    }

    public String getUid() {
        if (uid.isEmpty()) {
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

    public long getCurrent() {
        return current;
    }

    public long getLength() {
        return length;
    }

    public String getRemoteUrl() {
        return remoteUrl;
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

    public void setCurrent(long current){
        this.current = current;
    }

    public void setPort(int port){
        this.port = port;
    }
}
