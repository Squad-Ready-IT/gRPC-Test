package uk.startup.grpc.test.server.tasks;

import uk.startup.grpc.test.GetResourceDataRequestOrBuilder;
import uk.startup.grpc.test.server.exceptions.LoadDataException;
import uk.startup.grpc.test.server.providers.DataProvider;
import uk.startup.grpc.test.server.providers.ResourceData;
import uk.startup.grpc.test.logs.LogService;
import uk.startup.grpc.test.server.services.senders.DataSender;

public class LoadResourceDataTask implements Runnable {

    private GetResourceDataRequestOrBuilder request;
    private DataProvider dataProvider;
    private DataSender dataSender;
    private LogService log;

    public LoadResourceDataTask(
            GetResourceDataRequestOrBuilder request,
            DataProvider dataProvider,
            DataSender dataSender,
            LogService log
    ) {
        this.request = request;
        this.dataProvider = dataProvider;
        this.dataSender = dataSender;
        this.log = log;
    }

    @Override
    public void run() {
        String requestId = request.getRequestId();
        String resourceUrl = request.getResourceUrl();

        ResourceData resourceData;
        try {
            resourceData = dataProvider.load(resourceUrl);
        } catch (LoadDataException exc) {
            log.error(exc.getMessage(), exc);
            dataSender.sendError(requestId, exc);
            return;
        }
        dataSender.sendData(requestId, resourceData);
    }
}
