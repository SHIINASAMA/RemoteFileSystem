package pers.kaoru.rfs.core.log;

import java.util.LinkedList;
import java.util.List;

public class Logger {

    private final List<ImplAppender> appenderList;
    private final EventBuilder eventBuilder;

    public Logger() {
        appenderList = new LinkedList<>();
        eventBuilder = new EventBuilder();
    }

    public void add(ImplAppender appender) {
        appenderList.add(appender);
    }

    private void log(Level leve, Object message) {
        var e = eventBuilder.build(leve, message);
        for (var appender : appenderList) {
            appender.write(e);
        }
    }

    public void debug(Object message) {
        log(Level.DEBUG, message);
    }

    public void info(Object message) {
        log(Level.INFO, message);
    }

    public void warn(Object message) {
        log(Level.WARN, message);
    }

    public void error(Object message) {
        log(Level.ERROR, message);
    }
}