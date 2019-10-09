package uk.startup.grpc.test.client.services;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import uk.startup.grpc.test.*;
import uk.startup.grpc.test.client.models.ResourceData;
import uk.startup.grpc.test.client.stores.ResourceDataStore;
import uk.startup.grpc.test.logs.LogService;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Receives requested resource data (headers, body) from server
 */
public class ResourceDataService extends ResourceDataClientGrpc.ResourceDataClientImplBase {

    /** Stores all in-progress requests **/
    private final ResourceDataStore store;
    private final Writer writer;
    private final LogService log;
    private final CountDownLatch latch;


    public ResourceDataService(
            ResourceDataStore store,
            Writer writer,
            LogService log,
            CountDownLatch latch
    ) {
        this.store = store;
        this.writer = writer;
        this.log = log;
        this.latch = latch;
    }


    /**
     * Receives requested resource's headers from server
     * @param request contains request id and resource's headers
     * @param responseObserver gRPC response processor
     */
    @Override
    public void sendResourceHeaders(
            ResourceHeaders request,
            StreamObserver<VoidResponse> responseObserver
    ) {
        try {
            String requestId = request.getRequestId();
            Map<String, String> headers = request.getHeadersMap();

            ResourceData data = store.get(requestId);
            if (data == null) {
                Status status = Status.CANCELLED
                        .withDescription(String.format(
                                "Request(%s) was canceled", request.getRequestId()
                        ));
                log.warn(status);
                responseObserver.onError(status.asException());
                return;
            }
            data.setHeaders(headers);

            responseObserver.onNext(VoidResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception exc) {
            Status status = Status.INTERNAL
                    .withDescription("Internal Error")
                    .withCause(exc);

            log.error(status);

            responseObserver.onError(status.asException());
        }
    }

    /**
     * Creates observer to handling stream of resource body data
     * @param responseObserver gRPC response processor
     * @return stream observer
     */
    @Override
    public StreamObserver<ResourceBody> sendResourceBody(
            StreamObserver<StatusResponse> responseObserver
    ) {
        return new ResourceBodyObserver(responseObserver);
    }

    /**
     * Handles request errors
     * @param request contains request id and server errors
     * @param responseObserver gRPC response processor
     */
    @Override
    public void onErrors(RequestErrors request, StreamObserver<VoidResponse> responseObserver) {
        try {
            ResourceData data = store.get(request.getRequestId());
            if (data == null) {
                Status status = Status.CANCELLED
                        .withDescription(String.format(
                                "Request(%s) was canceled", request.getRequestId()
                        ));
                log.warn(status);

                responseObserver.onError(status.asException());
                return;
            }

            writer.writeErrors(request.getErrorsList());

            responseObserver.onNext(VoidResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception exc) {
            Status status = Status.INTERNAL
                    .withDescription("Internal Error")
                    .withDescription(exc.getMessage());

            log.error(status);

            responseObserver.onError(status.asException());
        } finally {
            latch.countDown();
        }
    }

    /**
     * Completes opened request
     * @param request contains request id
     * @param responseObserver gRPC response processor
     */
    @Override
    public void onCompleted(RequestCompleted request, StreamObserver<VoidResponse> responseObserver) {
        try {
            ResourceData data = store.remove(request.getRequestId());
            if (data == null) {
                Status status = Status.CANCELLED
                        .withDescription(String.format(
                                "Request(%s) was canceled", request.getRequestId()
                        ));
                log.warn(status);

                responseObserver.onError(status.asException());
                return;
            }
            responseObserver.onNext(VoidResponse.newBuilder().build());
            responseObserver.onCompleted();

            writer.writeData(data);
        } finally {
            latch.countDown();
        }
    }


    private class ResourceBodyObserver implements StreamObserver<ResourceBody> {

        private StreamObserver<StatusResponse> responseObserver;


        ResourceBodyObserver(StreamObserver<StatusResponse> responseObserver) {
            this.responseObserver = responseObserver;
        }


        @Override
        public void onNext(ResourceBody resourceBody) {
            ResourceData data = store.get(resourceBody.getRequestId());
            if (data == null) {
                Status status = Status.CANCELLED
                        .withDescription(String.format(
                                "Request(%s) was canceled", resourceBody.getRequestId()
                        ));
                log.warn(status);
                responseObserver.onError(status.asException());
                return;
            }

            data.appendToBody(
                    resourceBody.getChunk().toByteArray(),
                    resourceBody.getSize()
            );
        }

        @Override
        public void onError(Throwable exc) {
            try {
                log.error(Status.fromThrowable(exc));

                StatusResponse response = StatusResponse.newBuilder()
                        .setStatus(false)
                        .setError(exc.getMessage())
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } finally {
                latch.countDown();
            }
        }

        @Override
        public void onCompleted() {
            StatusResponse response = StatusResponse.newBuilder()
                    .setStatus(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
