package uk.startup.grpc.test.server.services.factories;

public interface TaskFactory<T> {

    Runnable create(T arg);
}
