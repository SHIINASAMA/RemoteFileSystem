package pers.kaoru.rfs.client.transmission;

public enum TaskState {
    // 任务正在运行
    RUNNING,
    // 任务暂停
    PAUSED,
    // 任务取消
    CANCELED,
    // 任务失败
    FAILED,
    // 任务完成
    FINISH
}
