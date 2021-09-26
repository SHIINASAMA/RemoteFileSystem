package pers.kaoru.rfs.client.transmission;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.*;

public class TaskDispatcher extends Thread {

    private final Semaphore permit;
    private final ExecutorService executorService;
    private final HashMap<String, Task> taskHashMap = new HashMap<>();
    private final ImplTaskListener listener;

    private final BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
    private volatile boolean isQuit = false;

    private static TaskDispatcher instance;

    public static TaskDispatcher init(int maxTaskAmount, String host, int port, String token, ImplTaskListener listener) {
        if (instance == null) {
            instance = new TaskDispatcher(maxTaskAmount, host, port, token, listener);
        }
        return instance;
    }

    public static TaskDispatcher get() {
        return instance;
    }

    private TaskDispatcher(int maxTaskAmount, String host, int port, String token, ImplTaskListener listener) {
        permit = new Semaphore(maxTaskAmount);
        executorService = new ThreadPoolExecutor(maxTaskAmount, maxTaskAmount, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, "task_thread"));
        this.listener = listener;
        start();
    }

    public void quit() {
        isQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                var task = taskQueue.take();
                getPermit().acquire();
                start(task);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
                if (isQuit) {
                    return;
                }
            }
        }
    }

    public Semaphore getPermit() {
        return permit;
    }

    public void start(Task task) {
        task.setState(TaskState.RUNNING);
        executorService.execute(task);
        onStart(task.getRecord());
    }

    public String add(Task task) {
        var id = task.getRecord().getUid();
        taskHashMap.put(id, task);
        taskQueue.add(task);
        return id;
    }

    public boolean pause(String taskId) {
        var task = taskHashMap.get(taskId);
        if (task != null) {
            if (task.getState() == TaskState.RUNNING) {
                task.setState(TaskState.PAUSED);
//                listener.onPaused(task.getRecord());
                onPaused(task.getRecord());
                return true;
            }
        }
        return false;
    }

    public boolean cancel(String taskId) {
        var task = taskHashMap.get(taskId);
        if (task != null) {
            task.setState(TaskState.CANCELED);
//            listener.onCanceled(task.getRecord());
            onCanceled(task.getRecord());
            return true;
        }
        return false;
    }

    public boolean resume(String taskId) {
        var task = taskHashMap.get(taskId);
        if (task != null && task.getState() == TaskState.PAUSED) {
            task.setState(TaskState.RUNNING);
            onResume(task.getRecord());
            taskQueue.add(task);
            return true;
        }
        return false;
    }

    public void onProgress(TaskRecord record, long speed) {
        listener.onProgress(record, speed);
    }

    public void onFailed(TaskRecord record, String error) {
        permit.release();
        taskHashMap.remove(record.getUid());
        listener.onFailed(record, error);
    }

    public void onPaused(TaskRecord record) {
        permit.release();
        listener.onPaused(record);
    }

    public void onStart(TaskRecord record) {
        listener.onStart(record);
    }

    public void onResume(TaskRecord record) {
        listener.onResume(record);
    }

    public void onFinish(TaskRecord record) {
        permit.release();
        taskHashMap.remove(record.getUid());
        listener.onFinish(record);
    }

    public void onCanceled(TaskRecord record) {
        permit.release();
        taskHashMap.remove(record.getUid());
        listener.onCanceled(record);
    }

    public void save(LinkedList<TaskRecord> otherTask) {
        quit();
        LinkedList<TaskRecord> records = new LinkedList<>();
        for (var task : taskHashMap.values()) {
            task.setState(TaskState.PAUSED);
            records.add(task.getRecord());
        }

        records.addAll(otherTask);

        try {
            OutputStream outputStream = new FileOutputStream("./tasks.data");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(records);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<TaskRecord> load() {
        try {
            InputStream inputStream = new FileInputStream("./tasks.data");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            LinkedList<TaskRecord> records = (LinkedList<TaskRecord>) objectInputStream.readObject();
            objectInputStream.close();
            return records;
        } catch (IOException | ClassNotFoundException exception) {
            return new LinkedList<>();
        }
    }
}