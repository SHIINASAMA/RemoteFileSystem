package pers.kaoru.rfs.core.log;

public interface ImplFormatter {
    /**
     * 将事件格式化为字符串
     * @param event 事件
     * @return 字符串
     */
    String format(Event event);
}
