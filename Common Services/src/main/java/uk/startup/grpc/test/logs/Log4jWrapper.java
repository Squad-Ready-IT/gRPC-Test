package uk.startup.grpc.test.logs;

public class Log4jWrapper implements LogService {

    private org.apache.log4j.Logger log;

    public Log4jWrapper(Class<?> _class) {
        this.log =  org.apache.log4j.Logger.getLogger(_class);
    }

    @Override
    public void info(Object message) {
        log.info(message);
    }

    @Override
    public void warn(Object message) {
        log.warn(message);
    }

    @Override
    public void error(Object message) {
        log.error(message);
    }

    @Override
    public void error(Object message, Throwable error) {
        log.error(message, error);
    }
}
