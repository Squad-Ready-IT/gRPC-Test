package uk.startup.grpc.test.server.tasks;

import org.junit.Before;
import org.junit.Test;
import uk.startup.grpc.test.GetResourceDataRequestOrBuilder;
import uk.startup.grpc.test.server.exceptions.LoadDataException;
import uk.startup.grpc.test.server.providers.DataProvider;
import uk.startup.grpc.test.server.providers.ResourceData;
import uk.startup.grpc.test.logs.LogService;
import uk.startup.grpc.test.server.services.senders.DataSender;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class LoadResourceDataTaskTest {

    private GetResourceDataRequestOrBuilder request;
    private DataProvider dataProvider;
    private DataSender dataSender;
    private LogService log;

    private Runnable task;


    @Before
    public void setUp() {
        request = mock(GetResourceDataRequestOrBuilder.class);
        dataProvider = mock(DataProvider.class);
        dataSender = mock(DataSender.class);
        log = mock(LogService.class);

        task = new LoadResourceDataTask(request, dataProvider, dataSender, log);
    }


    @Test
    public void test_RunAndSendData() throws LoadDataException {
        String requestId = "test-request-id";
        String resourceUrl = "test-resource-url";

        Map<String, String> headers = new HashMap<String, String>() {{
            put("header_1", "one");
            put("header_2", "two");
        }};
        byte[] body = "Test Body".getBytes();

        ResourceData data = new ResourceData(headers, body);

        when(request.getRequestId()).thenReturn(requestId);
        when(request.getResourceUrl()).thenReturn(resourceUrl);

        when(dataProvider.load(eq(resourceUrl))).thenReturn(data);

        task.run();

        verify(request, times(1)).getRequestId();
        verify(request, times(1)).getResourceUrl();
        verify(dataProvider, times(1)).load(eq(resourceUrl));
        verify(dataSender, times(1)).sendData(eq(requestId), eq(data));
    }

    @Test
    public void test_RunAndSendError() throws LoadDataException {
        String requestId = "test-request-id";
        String resourceUrl = "test-resource-url";
        Throwable error = new LoadDataException("Test Error");

        when(request.getRequestId()).thenReturn(requestId);
        when(request.getResourceUrl()).thenReturn(resourceUrl);


        when(dataProvider.load(eq(resourceUrl))).thenThrow(error);

        task.run();

        verify(request, times(1)).getRequestId();
        verify(request, times(1)).getResourceUrl();
        verify(dataProvider, times(1)).load(eq(resourceUrl));
        verify(dataSender, times(1)).sendError(eq(requestId), eq(error));
    }
}
