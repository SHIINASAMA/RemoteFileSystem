package pers.kaoru.rfs.core.log;

public class DefaultAppender implements ImplAppender {

    private final ImplFormatter formatter;
    private final Level threshold;

    public DefaultAppender(ImplFormatter formatter, Level threshold) {
        this.formatter = formatter;
        this.threshold = threshold;
    }

    @Override
    public void write(Event event) {
        if (event.getLevel().compareTo(threshold) >= 0) {
            System.out.println(formatter.format(event));
        }
    }
}
