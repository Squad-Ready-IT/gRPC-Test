package uk.startup.grpc.test.server.services.senders;

import com.google.protobuf.ByteString;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import uk.startup.grpc.test.*;
import uk.startup.grpc.test.logs.LogService;
import uk.startup.grpc.test.server.providers.ResourceData;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.function.Consumer;

public class ResourceDataSender implements DataSender {

    private ResourceDataClientGrpc.ResourceDataClientBlockingStub stub;
    private ResourceDataClientGrpc.ResourceDataClientStub asyncStub;

    private int maxChunkSize;

    private LogService log;

    public ResourceDataSender(Channel channel, int maxChunkSize, LogService log) {
        this.stub = ResourceDataClientGrpc.newBlockingStub(channel);
        this.asyncStub = ResourceDataClientGrpc.newStub(channel);
        this.maxChunkSize = maxChunkSize;
        this.log = log;
    }


    @Override
    public void sendData(String id, ResourceData data) {
        this.sendHeaders(id, data.getHeaders());
        this.sendBody(id, data.getBody());
    }

    @Override
    public void sendError(String id, Throwable error) {
        RequestErrors errors = RequestErrors.newBuilder()
                .setRequestId(id)
                .addErrors(error.getMessage())
                .build();

        try {
            stub.onErrors(errors);
        } catch (Exception exc) {
            log.error(Status.fromThrowable(exc));
        }
    }


    private void sendHeaders(String requestId, Map<String, String> headers) {
        ResourceHeaders resourceHeaders = ResourceHeaders.newBuilder()
                .setRequestId(requestId)
                .putAllHeaders(headers)
                .build();

        try {
            stub.sendResourceHeaders(resourceHeaders);
        } catch (Exception exc) {
            Status status = Status.fromThrowable(exc);
            log.info("Send headers operation(%s) is failed: " + status);
            throw exc;
        }
    }

    private void sendBody(String requestId, byte[] body) {
        StreamObserver<ResourceBody> bodyObserver =
                asyncStub.sendResourceBody(this.getResourceBodyObserver(requestId));

        try {
            this.sendBody(bodyObserver, requestId, body);
        } catch (Exception exc) {
            log.error("Send body is failed: " + exc.getMessage());
            bodyObserver.onError(exc);
        }
    }

    private StreamObserver<StatusResponse> getResourceBodyObserver(String requestId) {
        Consumer<StatusResponse> onNext = (response) -> {
            log.info(String.format(
                    "Send body operation(%s), status: %b; error: %s",
                    requestId,
                    response.getStatus(),
                    response.getError()
            ));
        };
        Consumer<Throwable> onError = (error) -> {
            log.error(String.format(
                    "Send body operation(%s) is failed: %s",
                    requestId,
                    Status.fromThrowable(error)
            ));
        };
        Runnable onComplete = () -> {
            RequestCompleted completed = RequestCompleted.newBuilder()
                    .setRequestId(requestId)
                    .build();

            try {
                stub.onCompleted(completed);
            } catch (Exception exc) {
                log.error(Status.fromThrowable(exc));
            }
            log.info(String.format("Send body operation(%s) is completed", requestId));
        };

        return new BodyResponseObserver(onNext, onError, onComplete);
    }

    private void sendBody(
            StreamObserver<ResourceBody> observer,
            String requestId,
            byte[] body
    ) {
        int bodyLength = body.length;
        ByteBuffer chunk = ByteBuffer.allocate(maxChunkSize);

        int offset = 0;
        while (offset < bodyLength) {
            chunk.clear();

            int chunkSize = Math.min(bodyLength - offset, maxChunkSize);
            chunk.put(body, offset, chunkSize);

            ResourceBody resourceBody = ResourceBody.newBuilder()
                    .setRequestId(requestId)
                    .setChunk(ByteString.copyFrom(chunk.array()))
                    .setSize(chunkSize)
                    .build();

            observer.onNext(resourceBody);

            offset += chunkSize;
        }

        observer.onCompleted();
    }
}
