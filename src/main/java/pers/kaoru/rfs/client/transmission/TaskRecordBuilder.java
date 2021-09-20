package pers.kaoru.rfs.client.transmission;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class TaskRecordBuilder {

    private final String host;
    private final int port;
    private final String token;

    public TaskRecordBuilder(String host, int port, String token) {
        this.host = host;
        this.port = port;
        this.token = token;
    }

    public TaskRecord build(String remoteUrl, String localUrl, TaskType type) {
        return new TaskRecord(host, port, token, remoteUrl, localUrl, type);
    }

    public List<TaskRecord> build(String path) {
        try {
            InputStream inputStream = new FileInputStream("tasks");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            var records = (LinkedList<TaskRecord>) objectInputStream.readObject();
            objectInputStream.close();
            return records;
        } catch (Exception exception) {
            return new LinkedList<>();
        }
    }
}
