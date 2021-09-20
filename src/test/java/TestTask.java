import pers.kaoru.rfs.client.transmission.*;

@Deprecated
public class TestTask {

    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6IjIwMmNiOTYyYWM1OTc1Yjk2NGI3MTUyZDIzNGI3MCIsImV4cCI6MTYzNTE4MjU5OCwidXNlcm5hbWUiOiJyb290In0.xBpmioYDJ5MYS8XfR7tIh43aK-h-pq-dv30bD1FVJi8";
    private static Task task;

    public static void main(String[] args) {
        TaskRecordBuilder builder = new TaskRecordBuilder("localhost", 8080, TOKEN);
        var record = builder.build("/FILE", "/Downloads/心灵终结3.3.5中文整合包.exe", 0, TaskType.UPLOAD);

//        task = new Task(record, 0, new ImplTaskListener() {
//            @Override
//            public void onProgress(TaskRecord record, long speed) {
//                System.out.println(BitCount.ToString(speed) + "/S");
//            }
//
//            @Override
//            public void onNewTask(TaskRecord record) {
//
//            }
//
//            @Override
//            public void onFailed(TaskRecord record, String error) {
//
//            }
//
//            @Override
//            public void onPaused(TaskRecord record) {
//
//            }
//
//            @Override
//            public void onStart(TaskRecord record) {
//                System.out.println(record.getCurrent());
//            }
//
//            @Override
//            public void onResume(TaskRecord record) {
//
//            }
//
//            @Override
//            public void onFinish(TaskRecord record) {
//                System.out.println("Done");
//            }
//
//            @Override
//            public void onCanceled(TaskRecord record) {
//
//            }
//        });
//        Thread thread = new Thread(task);
//        thread.start();
        System.out.println("MAIN");
    }
}
