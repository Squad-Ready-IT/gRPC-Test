package uk.startup.grpc.test.server.providers;

import java.util.Map;

public class ResourceData {

    private Map<String, String> headers;
    private byte[] body;

    public ResourceData(Map<String, String> headers, byte[] body) {
        this.headers = headers;
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
