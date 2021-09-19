package pers.kaoru.rfs.client.transmission;

public interface ImplTaskListener {

    /**
     * 任务进度更新
     * @param record 任务记录
     * @param speed 每秒传输字节
     */
    void onProgress(TaskRecord record, long speed);

    /**
     * 新添加任务
     * @param record 任务记录
     */
    void onNewTask(TaskRecord record);

    /**
     * 任务失败
     * @param record 任务记录
     */
    void onFailed(TaskRecord record);

    /**
     * 任务暂停
     * @param record 任务记录
     */
    void onPaused(TaskRecord record);

    /**
     * 任务开始
     * @param record 任务记录
     */
    void onStart(TaskRecord record);

    /**
     * 任务恢复
     * @param record 任务记录
     */
    void onResume(TaskRecord record);

    /**
     * 任务完成
     * @param record 任务记录
     */
    void onFinish(TaskRecord record);

    /**
     * 任务取消
     * @param record 任务记录
     */
    void onCanceled(TaskRecord record);
}
