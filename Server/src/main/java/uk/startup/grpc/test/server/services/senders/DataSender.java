package uk.startup.grpc.test.server.services.senders;

import uk.startup.grpc.test.server.providers.ResourceData;

public interface DataSender {

    void sendData(String id, ResourceData data);

    void sendError(String id, Throwable error);
}
