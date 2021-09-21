package pers.kaoru.rfs.core.log;

public class EventBuilder {

    private StackTraceElement[] stackTrace;

    private void updateStackTrace() {
        stackTrace = new Throwable().getStackTrace();
    }

    private int getLineNumber() {
        return stackTrace[1].getLineNumber();
    }

    private String getMethodName() {
        return stackTrace[1].getMethodName();
    }

    private String getFileName() {
        return stackTrace[1].getFileName();
    }

    private String getClassName() {
        return stackTrace[1].getClassName();
    }

    public Event build(Level level, Object message) {
        String msg;
        if (message instanceof Enum) {
            msg = ((Enum<?>) message).name();
        } else {
            msg = message.toString();
        }
        updateStackTrace();
        String fileName = getFileName();
        int lineNumber = getLineNumber();
        String thread = Thread.currentThread().getName();
        long date = System.currentTimeMillis();
        return new Event(level, fileName, lineNumber, thread, date, msg);
    }
}
