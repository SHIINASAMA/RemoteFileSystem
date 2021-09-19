import pers.kaoru.rfs.client.BitCount;
import pers.kaoru.rfs.client.transmission.*;

public class TestTask {

    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6IjIwMmNiOTYyYWM1OTc1Yjk2NGI3MTUyZDIzNGI3MCIsImV4cCI6MTYzNTE4MjU5OCwidXNlcm5hbWUiOiJyb290In0.xBpmioYDJ5MYS8XfR7tIh43aK-h-pq-dv30bD1FVJi8";
    private static Task task;

    public static void main(String[] args) {
        TaskBuilder builder = new TaskBuilder("localhost", 8080);
        var record = builder.build("/Music/yousa.mp3", 0, TaskType.DOWNLOAD);

        task = new Task(record, 0, TOKEN, new ImplTaskListener() {
            @Override
            public void onProgress(TaskRecord record, long speed) {
                System.out.println(BitCount.ToString(speed));
            }

            @Override
            public void onNewTask(TaskRecord record) {

            }

            @Override
            public void onFailed(TaskRecord record) {

            }

            @Override
            public void onPaused(TaskRecord record) {

            }

            @Override
            public void onStart(TaskRecord record) {
                System.out.println(record.getCurrent());
            }

            @Override
            public void onResume(TaskRecord record) {

            }

            @Override
            public void onFinish(TaskRecord record) {
                System.out.println("Done");
            }

            @Override
            public void onCanceled(TaskRecord record) {

            }
        });
        Thread thread = new Thread(task);
        thread.start();
    }
}
