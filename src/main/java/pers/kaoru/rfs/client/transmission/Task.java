package pers.kaoru.rfs.client.transmission;

import pers.kaoru.rfs.core.web.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;

public class Task implements Runnable {

    private final TaskRecord record;
    private final long start;
    private volatile TaskState state;
    private final String token;

    public Task(TaskRecord record, long start) {
        this.record = record;
        this.start = start;
        this.token = record.getToken();
        state = TaskState.INIT;
    }

    public TaskRecord getRecord() {
        return record;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    @Override
    public void run() {
        if (record.getType() == TaskType.DOWNLOAD) {
            Range range = null;
            if (record.getLength() == 0) {
                Request request = new Request();
                request.setMethod(RequestMethod.DOWNLOAD);
                request.setHeader("source", record.getRemoteUrl());
                request.setHeader("range", new Range(0L, 0L, 0L).toString());
                request.setHeader("token", token);
                Response response = null;
                try {
                    Socket socket = new Socket(record.getHost(), record.getPort());
                    WebUtils.WriteRequest(socket, request);
                    response = WebUtils.ReadResponse(socket);
                } catch (IOException exception) {
                    exception.printStackTrace();
                    state = TaskState.FAILED;
                    TaskDispatcher.get().onFailed(record, exception.getMessage());
                    return;
                }

                if (response.getCode() == ResponseCode.OK) {
                    String rangeStr = response.getHeader("range");
                    assert rangeStr != null;
                    Range temp = Range.RangeBuild(rangeStr);
                    range = new Range(start, temp.getTotal() - 1, temp.getTotal());
                    record.setLength(range.getTotal());
                } else {
                    TaskDispatcher.get().onFailed(record, response.getHeader("error"));
                    return;
                }
            } else {
                range = new Range(record.getCurrent(), record.getLength() - 1, record.getLength());
            }

            try {
                Request request = new Request();
                request.setMethod(RequestMethod.DOWNLOAD);
                request.setHeader("source", record.getRemoteUrl());
                request.setHeader("range", range.toString());
                request.setHeader("token", token);
                Socket socket = new Socket(record.getHost(), record.getPort());
                WebUtils.WriteRequest(socket, request);
                Response response = WebUtils.ReadResponse(socket);

                var webStream = socket.getInputStream();
                state = TaskState.RUNNING;
                try (var localStream = new FileOutputStream(record.getLocalUrl() + "/" + record.getName(), true)) {
                    long fPos = System.currentTimeMillis();
                    long sPos;
                    long speed = 0;

                    byte[] bytes = new byte[1024];
                    long count = range.getTotal();
                    while (count > 0) {
                        if (state != TaskState.RUNNING) {
                            return;
                        }
                        long current;
                        if (count > 1024) {
                            current = 1024;
                        } else {
                            current = count;
                        }
                        long length = webStream.read(bytes, 0, (int) current);
                        if (length == -1) {
                            break;
                        }
                        count -= length;
                        localStream.write(bytes, 0, (int) length);
                        record.setCurrent(record.getCurrent() + current);
                        sPos = System.currentTimeMillis();
                        if (sPos - fPos > 1000) {
                            fPos = sPos;
                            TaskDispatcher.get().onProgress(record, speed);
                            speed = 0;
                        } else {
                            speed += current;
                        }
                    }
                    state = TaskState.FINISH;
                    TaskDispatcher.get().onFinish(record);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                state = TaskState.FAILED;
                TaskDispatcher.get().onFailed(record, exception.getMessage());
            }
        } else {
            File file = new File(record.getLocalUrl());
            Range range = new Range(0L, file.length() - 1, file.length());
            Request request = new Request();
            request.setMethod(RequestMethod.UPLOAD);
            request.setHeader("source", record.getRemoteUrl());
            request.setHeader("range", range.toString());
            request.setHeader("token", token);

            try {
                Socket socket = new Socket(record.getHost(), record.getPort());
                WebUtils.WriteRequest(socket, request);
                Response response = WebUtils.ReadResponse(socket);
                if (response.getCode() == ResponseCode.OK) {
                    var webStream = socket.getOutputStream();
                    try (RandomAccessFile localStream = new RandomAccessFile(file, "r")) {
                        localStream.seek(start);

                        byte[] bytes = new byte[1024];
                        long fPos = System.currentTimeMillis();
                        long sPos;
                        long speed = 0;
                        int current;
                        while ((current = localStream.read(bytes)) > 0) {
                            webStream.write(bytes, 0, current);
                            record.setCurrent(record.getCurrent() + current);
                            sPos = System.currentTimeMillis();
                            if (sPos - fPos > 1000) {
                                fPos = sPos;
                                TaskDispatcher.get().onProgress(record, speed);
                                speed = 0;
                            } else {
                                speed += current;
                            }
                        }
                        state = TaskState.FINISH;
                        TaskDispatcher.get().onFinish(record);
                    }
                } else {
                    state = TaskState.FAILED;
                    TaskDispatcher.get().onFailed(record, response.getHeader("error"));
                }
            } catch (IOException exception) {
                state = TaskState.FAILED;
                TaskDispatcher.get().onFailed(record, exception.getMessage());
            }
        }
    }
}
