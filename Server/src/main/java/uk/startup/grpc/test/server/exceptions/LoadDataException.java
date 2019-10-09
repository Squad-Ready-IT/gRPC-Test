package uk.startup.grpc.test.server.exceptions;

public class LoadDataException extends Exception {

    public LoadDataException(String message) {
        super(message);
    }

    public LoadDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
