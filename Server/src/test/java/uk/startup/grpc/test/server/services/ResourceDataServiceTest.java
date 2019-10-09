package uk.startup.grpc.test.server.services;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import uk.startup.grpc.test.GetResourceDataRequest;
import uk.startup.grpc.test.GetResourceDataRequestOrBuilder;
import uk.startup.grpc.test.VoidResponse;
import uk.startup.grpc.test.server.services.factories.TaskFactory;
import uk.startup.grpc.test.logs.LogService;

import java.util.concurrent.Executor;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

public class ResourceDataServiceTest {

    private Executor taskExecutor;
    private TaskFactory<GetResourceDataRequestOrBuilder> taskFactory;
    private LogService log;
    private ResourceDataService service;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        taskExecutor = mock(Executor.class);
        taskFactory = mock(TaskFactory.class);
        log = mock(LogService.class);

        service = new ResourceDataService(taskExecutor, taskFactory, log);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_GetResourceData_SuccessResponse() {
        String requestId = "test-request-id";
        String resourceUrl = "test-resource-url";

        StreamObserver<VoidResponse> responseObserver = mock(StreamObserver.class);

        GetResourceDataRequest request = GetResourceDataRequest.newBuilder()
                .setRequestId(requestId)
                .setResourceUrl(resourceUrl)
                .build();

        Runnable task = mock(Runnable.class);
        when(taskFactory.create(eq(request))).thenReturn(task);

        service.getResourceData(request, responseObserver);

        VoidResponse expectedResponse = VoidResponse.newBuilder().build();

        verify(taskFactory, times(1)).create(eq(request));
        verify(taskExecutor, times(1)).execute(eq(task));
        verify(responseObserver, times(1)).onNext(eq(expectedResponse));
        verify(responseObserver, times(1)).onCompleted();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_GetResourceData_FailResponse() {
        String requestId = "test-request-id";
        String resourceUrl = "test-resource-url";
        Throwable error = new RuntimeException("Test Exception");

        StreamObserver<VoidResponse> responseObserver = mock(StreamObserver.class);

        GetResourceDataRequest request = GetResourceDataRequest.newBuilder()
                .setRequestId(requestId)
                .setResourceUrl(resourceUrl)
                .build();

        Runnable task = mock(Runnable.class);
        when(taskFactory.create(eq(request))).thenReturn(task);
        doThrow(error).when(taskExecutor).execute(eq(task));

        service.getResourceData(request, responseObserver);

        ArgumentCaptor<Throwable> errorCaptor =
                ArgumentCaptor.forClass(Throwable.class);

        verify(taskFactory, times(1)).create(eq(request));
        verify(taskExecutor, times(1)).execute(eq(task));
        verify(responseObserver, times(1)).onError(errorCaptor.capture());

        StatusException expectedError = Status.UNKNOWN
                .withCause(error)
                .withDescription(error.getMessage())
                .asException();

        assertTrue(errorCaptor.getValue() instanceof StatusException);
        StatusException actualError = (StatusException) errorCaptor.getValue();
        assertEquals(expectedError.getStatus().getCode(), actualError.getStatus().getCode());
        assertEquals(expectedError.getStatus().getCause(), actualError.getStatus().getCause());
        assertEquals(expectedError.getStatus().getDescription(), actualError.getStatus().getDescription());
        assertEquals(expectedError.getMessage(), actualError.getMessage());
    }
}
