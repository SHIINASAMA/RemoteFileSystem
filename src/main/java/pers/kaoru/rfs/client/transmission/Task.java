package pers.kaoru.rfs.client.transmission;

import pers.kaoru.rfs.core.web.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Task implements Runnable {

    private TaskRecord record;
    private long start;
    private ImplTaskListener listener;
    private final String token;
    private volatile TaskState state;

    public Task(TaskRecord record, long start, String token, ImplTaskListener listener) {
        this.record = record;
        this.start = start;
        this.token = token;
        this.listener = listener;
        state = TaskState.INIT;
    }

    synchronized public void setState(TaskState state) {
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
                    listener.onFailed(record);
                    return;
                }
                if (response == null) {
                    state = TaskState.FAILED;
                    listener.onFailed(record);
                    return;
                }
                if (response.getCode() == ResponseCode.OK) {
                    String rangeStr = response.getHeader("range");
                    assert rangeStr != null;
                    Range temp = Range.RangeBuild(rangeStr);
                    range = new Range(start, temp.getTotal() - 1, temp.getTotal());
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
                try (var localStream = new FileOutputStream(record.getName(), true)) {
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
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        count -= length;
                        localStream.write(bytes, 0, (int) length);
                        record.setCurrent(record.getCurrent() + current);
                        sPos = System.currentTimeMillis();
                        if (sPos - fPos > 1000) {
                            fPos = sPos;
                            listener.onProgress(record, speed);
                            speed = 0;
                        } else {
                            speed += current;
                        }
                    }
                    state = TaskState.FINISH;
                    listener.onFinish(record);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                state = TaskState.FAILED;
                listener.onFailed(record);
            }
        } else {
        }
    }
}
