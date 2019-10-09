package uk.startup.grpc.test.logs;

public interface LogService {

    void info(Object message);

    void warn(Object message);

    void error(Object message);

    void error(Object message, Throwable error);
}
