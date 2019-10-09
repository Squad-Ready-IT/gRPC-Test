package uk.startup.grpc.test.server.services.factories;

import uk.startup.grpc.test.GetResourceDataRequestOrBuilder;
import uk.startup.grpc.test.server.providers.DataProvider;
import uk.startup.grpc.test.logs.LogService;
import uk.startup.grpc.test.server.services.senders.DataSender;
import uk.startup.grpc.test.server.tasks.LoadResourceDataTask;

public class LoadResourceDataTaskFactory implements TaskFactory<GetResourceDataRequestOrBuilder> {

    private DataProvider dataProvider;
    private DataSender dataSender;
    private LogService log;

    public LoadResourceDataTaskFactory(
            DataProvider dataProvider,
            DataSender dataSender,
            LogService log
    ) {
        this.dataProvider = dataProvider;
        this.dataSender = dataSender;
        this.log = log;
    }

    @Override
    public Runnable create(GetResourceDataRequestOrBuilder request) {
        return new LoadResourceDataTask(request, dataProvider, dataSender, log);
    }
}
