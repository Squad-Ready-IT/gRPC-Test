package uk.startup.grpc.test.server.services;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import uk.startup.grpc.test.GetResourceDataRequest;
import uk.startup.grpc.test.GetResourceDataRequestOrBuilder;
import uk.startup.grpc.test.ResourceDataServerGrpc;
import uk.startup.grpc.test.VoidResponse;
import uk.startup.grpc.test.server.services.factories.TaskFactory;
import uk.startup.grpc.test.logs.LogService;

import java.util.concurrent.Executor;


public class ResourceDataService extends ResourceDataServerGrpc.ResourceDataServerImplBase {

    private Executor taskExecutor;
    private TaskFactory<GetResourceDataRequestOrBuilder> taskFactory;
    private LogService log;

    public ResourceDataService(
            Executor taskExecutor,
            TaskFactory<GetResourceDataRequestOrBuilder> taskFactory,
            LogService log
    ) {
        this.taskExecutor = taskExecutor;
        this.taskFactory = taskFactory;
        this.log = log;
    }


    @Override
    public void getResourceData(
            GetResourceDataRequest request,
            StreamObserver<VoidResponse> responseObserver
    ) {
        try {
            String requestId = request.getRequestId();
            String resourceUrl = request.getResourceUrl();

            log.info("Request ID: " + requestId);
            log.info("Resource URL: " + resourceUrl);

            Runnable task = taskFactory.create(request);
            taskExecutor.execute(task);

            responseObserver.onNext(VoidResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            Status status = Status.UNKNOWN
                    .withCause(exc)
                    .withDescription(exc.getMessage());

            responseObserver.onError(status.asException());
        }
    }
}
