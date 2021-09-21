package pers.kaoru.rfs.core.log;

public class Event {

    private final Level level;
    private final String fileName;
    private final int lineNumber;
    private final String thread;
    private final long date;
    private final String message;

    public Event(Level level, String fileName, int lineNumber, String thread, long date, String message) {
        this.level = level;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.thread = thread;
        this.date = date;
        this.message = message;
    }

    public Level getLevel() {
        return level;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getThread() {
        return thread;
    }

    public long getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
}
