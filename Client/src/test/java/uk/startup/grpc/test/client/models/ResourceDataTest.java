package uk.startup.grpc.test.client.models;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class ResourceDataTest {

    @Test
    public void test_SetHeaders() {
        ResourceData data = new ResourceData();

        Map<String, String> headers1 = new HashMap<String, String>() {{
            put("header_1", "one");
        }};
        Map<String, String> headers2 = new HashMap<String, String>() {{
            put("header_2", "two");
        }};

        data.setHeaders(headers1);
        assertEquals(headers1, data.getHeaders());

        data.setHeaders(headers2);
        assertEquals(headers2, data.getHeaders());
    }

    @Test
    public void test_AppendToBody() {
        ResourceData data = new ResourceData();

        byte[] bodyChunk1 = "Body Chunk 1".getBytes();
        byte[] bodyChunk2 = " + ".getBytes();
        byte[] bodyChunk3 = "Body Chunk 3".getBytes();

        data.appendToBody(bodyChunk1, bodyChunk1.length);
        data.appendToBody(bodyChunk2, bodyChunk2.length);
        data.appendToBody(bodyChunk3, bodyChunk3.length);

        String expectedBody = "Body Chunk 1 + Body Chunk 3";

        assertEquals(expectedBody, data.getBody());
    }

    @Test
    public void test_EqualsAndHashCode() {
        ResourceData data1 = new ResourceData();
        ResourceData data2 = new ResourceData();

        Map<String, String> headers = new HashMap<String, String>() {{
            put("header_1", "one");
        }};
        byte[] body = "Resource Body".getBytes();

        data1.setHeaders(headers);
        data2.setHeaders(headers);

        data1.appendToBody(body, body.length);
        data2.appendToBody(body, body.length);

        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
    }
}
