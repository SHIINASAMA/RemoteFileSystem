package pers.kaoru.rfs.client.transmission;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;

public class TaskBuilder {

    private final String host;
    private final int port;

    public TaskBuilder(String host, int port) {
        this.host = host;
        this.port =port;
    }

    public TaskRecord build(String remoteUrl, long length, TaskType type) {
        return new TaskRecord(host, port, remoteUrl, length, type);
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
