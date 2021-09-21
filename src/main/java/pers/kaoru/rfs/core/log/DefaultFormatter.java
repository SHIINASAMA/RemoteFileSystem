package pers.kaoru.rfs.core.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultFormatter implements ImplFormatter {

    @Override
    public String format(Event event) {
        Date date = new Date(event.getDate());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

        return dateFormat.format(date) + '\t' +
                event.getLevel().name() + '\t' +
                event.getMessage();
    }
}
