package pers.kaoru.rfs.core.log;

public class LoggerManager {

    private static Logger instance;

    public static Logger get() {
        if(instance == null){
            instance = create();
        }
        return instance;
    }

    private static Logger create() {
        Logger logger = new Logger();
        DefaultFormatter defaultFormatter = new DefaultFormatter();
        DefaultAppender defaultAppender = new DefaultAppender(defaultFormatter, Level.DEBUG);
        logger.add(defaultAppender);
        return logger;
    }
}
