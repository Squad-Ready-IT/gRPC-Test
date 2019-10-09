package uk.startup.grpc.test.server;

import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ServerBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.startup.grpc.test.GetResourceDataRequestOrBuilder;
import uk.startup.grpc.test.server.providers.*;
import uk.startup.grpc.test.server.readers.BytesReader;
import uk.startup.grpc.test.server.readers.InputStreamReader;
import uk.startup.grpc.test.server.services.ResourceDataService;
import uk.startup.grpc.test.server.services.factories.LoadResourceDataTaskFactory;
import uk.startup.grpc.test.server.services.factories.TaskFactory;
import uk.startup.grpc.test.logs.Log4jWrapper;
import uk.startup.grpc.test.logs.LogService;
import uk.startup.grpc.test.server.services.senders.DataSender;
import uk.startup.grpc.test.server.services.senders.ResourceDataSender;
import uk.startup.grpc.test.server.tasks.LoadResourceDataTask;
import uk.startup.grpc.test.utils.PropertyProvider;

import java.io.IOException;
import java.util.concurrent.*;

public class GrpcServer {

    private static final int MAX_CHUNK_SIZE = 1024;
    private static final PropertyProvider PROPERTY_PROVIDER = new PropertyProvider("/application.properties");
    private static final LogService log = new Log4jWrapper(GrpcServer.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        BindableService service = getService();

        Integer serverPort = PROPERTY_PROVIDER.getIntProperty("server.port");
        System.out.println("gRPC server is starting on the port: " + serverPort);

        ServerBuilder.forPort(serverPort)
                .addService(service)
                .build()
                .start()
                .awaitTermination();
    }


    private static BindableService getService() {
        UrlConnectionProvider connectionProvider = new DefaultUrlConnectionProvider();
        InputStreamReader<byte[]> reader = new BytesReader();
        DataProvider httpDataProvider = new HttpDataProvider(connectionProvider, reader);

        DataSender resourceDataSender = new ResourceDataSender(
                getClientChannel(),
                MAX_CHUNK_SIZE,
                new Log4jWrapper(ResourceDataSender.class)
        );
        TaskFactory<GetResourceDataRequestOrBuilder> taskFactory = new LoadResourceDataTaskFactory(
                httpDataProvider,
                resourceDataSender,
                new Log4jWrapper(LoadResourceDataTask.class)
        );

        return new ResourceDataService(
                getTaskExecutor(),
                taskFactory,
                new Log4jWrapper(ResourceDataService.class)
        );
    }

    private static Executor getTaskExecutor() {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        return new ThreadPoolExecutor(1, 2, 10, TimeUnit.MINUTES, taskQueue);
    }

    private static ManagedChannel getClientChannel() {
        String clientHost = PROPERTY_PROVIDER.getStringProperty("client.host");
        Integer clientPort = PROPERTY_PROVIDER.getIntProperty("client.port");

        return ManagedChannelBuilder.forAddress(clientHost, clientPort)
                .usePlaintext()
                .build();
    }
}
