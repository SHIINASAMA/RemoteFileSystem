package pers.kaoru.rfs.core.log;

public interface ImplAppender {
    /**
     * 输出事件
     * @param event 目标事件
     */
    void write(Event event);
}
