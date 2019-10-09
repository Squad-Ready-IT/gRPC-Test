package uk.startup.grpc.test.server.services.senders;

import io.grpc.stub.StreamObserver;
import uk.startup.grpc.test.StatusResponse;

import java.util.function.Consumer;


public class BodyResponseObserver implements StreamObserver<StatusResponse> {

    private Consumer<StatusResponse> onNext;
    private Consumer<Throwable> onError;
    private Runnable onComplete;


    BodyResponseObserver(
            Consumer<StatusResponse> onNext,
            Consumer<Throwable> onError,
            Runnable onComplete
    ) {
        this.onNext = onNext;
        this.onError = onError;
        this.onComplete = onComplete;
    }

    @Override
    public void onNext(StatusResponse response) {
        onNext.accept(response);
    }

    @Override
    public void onError(Throwable error) {
        onError.accept(error);
    }

    @Override
    public void onCompleted() {
        onComplete.run();
    }
}
