package uk.startup.grpc.test.client.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Model of resource data (headers, body)
 */
public class ResourceData {

    private Map<String, String> headers = new HashMap<>();
    private StringBuilder body = new StringBuilder();

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body.toString();
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Appends new data to resource body
     * @param data resource data
     * @param length length of resource data
     */
    public void appendToBody(byte[] data, int length) {
        body.append(new String(data, 0, length));
    }

    @Override
    public int hashCode() {
        return headers.hashCode() + body.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) {
            return false;
        }
        ResourceData data = (ResourceData) o;
        return headers.equals(data.headers) &&
                body.toString().equals(data.body.toString());
    }
}
