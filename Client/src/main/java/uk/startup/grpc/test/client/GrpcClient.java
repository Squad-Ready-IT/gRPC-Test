package uk.startup.grpc.test.client;

import io.grpc.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.startup.grpc.test.GetResourceDataRequest;
import uk.startup.grpc.test.ResourceDataServerGrpc;
import uk.startup.grpc.test.client.models.ResourceData;
import uk.startup.grpc.test.client.services.ConsoleWriter;
import uk.startup.grpc.test.client.services.ResourceDataService;
import uk.startup.grpc.test.client.stores.InMemoryResourceDataStore;
import uk.startup.grpc.test.client.stores.ResourceDataStore;
import uk.startup.grpc.test.logs.Log4jWrapper;
import uk.startup.grpc.test.utils.PropertyProvider;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class GrpcClient {

    private static final PropertyProvider PROPERTY_PROVIDER = new PropertyProvider("/application.properties");

    private static final int MAX_PRINT_SIZE = 100;

    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    public static void main(String[] args) throws Exception {
        final ResourceDataStore store = new InMemoryResourceDataStore();

        CountDownLatch latch = startClient(store);

        ManagedChannel channel = null;
        try {
            String resourceUrl = getResourceUrl();
            channel = getManagedChannel();

            GetResourceDataRequest request = createRequest(resourceUrl);
            store.add(request.getRequestId(), new ResourceData());

            sendRequest(channel, request);
            latch.await();
        } finally {
            if (channel != null) {
                channel.shutdownNow();
            }
        }
    }


    private static CountDownLatch startClient(ResourceDataStore store) throws IOException {
        Integer clientPort = PROPERTY_PROVIDER.getIntProperty("client.port");

        CountDownLatch latch = new CountDownLatch(1);

        BindableService service = new ResourceDataService(
                store,
                new ConsoleWriter(MAX_PRINT_SIZE),
                new Log4jWrapper(ResourceDataService.class),
                latch
        );

        ServerBuilder.forPort(clientPort)
                .addService(service)
                .build()
                .start();

        return latch;
    }

    private static String getResourceUrl() {
        System.out.println("Enter resource URL: ");
        Scanner scanner = new Scanner(System.in);
        String resourceUrl = scanner. nextLine();

        if (resourceUrl == null || resourceUrl.isEmpty()) {
            System.out.println("Warning: invalid resource URL");
        }

        return resourceUrl;
    }

    private static ManagedChannel getManagedChannel() {
        String serverHost = PROPERTY_PROVIDER.getStringProperty("server.host");
        Integer serverPort = PROPERTY_PROVIDER.getIntProperty("server.port");

        return ManagedChannelBuilder.forAddress(serverHost, serverPort)
                .usePlaintext()
                .build();
    }

    private static GetResourceDataRequest createRequest(String resourceUrl) {
        String requestId = UUID.randomUUID().toString();

        return GetResourceDataRequest.newBuilder()
                .setRequestId(requestId)
                .setResourceUrl(resourceUrl)
                .build();
    }

    private static void sendRequest(
            Channel channel,
            GetResourceDataRequest request
    ) {
        ResourceDataServerGrpc.ResourceDataServerBlockingStub stub =
                ResourceDataServerGrpc.newBlockingStub(channel);

        stub.getResourceData(request);
    }
}
