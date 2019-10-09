package uk.startup.grpc.test.client.stores;

import org.junit.Before;
import org.junit.Test;
import uk.startup.grpc.test.client.models.ResourceData;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class InMemoryStoreTest {

    private ResourceDataStore store;


    @Before
    public void setUp() {
        store = new InMemoryResourceDataStore();
    }


    @Test
    public void test_AddNewDataAndGet() {
        String id1 = "testId_1";
        String id2 = "testId_2";
        Map<String, String> headers1 = new HashMap<String, String>() {{
           put("header_1", "one");
        }};
        Map<String, String> headers2 = new HashMap<String, String>() {{
            put("header_2", "two");
        }};
        byte[] body1 = "Test Body 1".getBytes();
        byte[] body2 = "Test Body 2".getBytes();

        ResourceData data1 = new ResourceData();
        data1.setHeaders(headers1);
        data1.appendToBody(body1, body1.length);

        ResourceData data2 = new ResourceData();
        data1.setHeaders(headers2);
        data1.appendToBody(body2, body2.length);

        store.add(id1, data1);
        store.add(id2, data2);

        assertEquals(data1, store.get(id1));
        assertEquals(data2, store.get(id2));
    }

    @Test
    public void test_RemoveDataById() {
        String id1 = "testId_1";
        String id2 = "testId_2";
        Map<String, String> headers1 = new HashMap<String, String>() {{
            put("header_1", "one");
        }};
        Map<String, String> headers2 = new HashMap<String, String>() {{
            put("header_2", "two");
        }};
        byte[] body1 = "Test Body 1".getBytes();
        byte[] body2 = "Test Body 2".getBytes();

        ResourceData data1 = new ResourceData();
        data1.setHeaders(headers1);
        data1.appendToBody(body1, body1.length);

        ResourceData data2 = new ResourceData();
        data1.setHeaders(headers2);
        data1.appendToBody(body2, body2.length);

        store.add(id1, data1);
        store.add(id2, data2);

        assertEquals(data1, store.get(id1));
        assertEquals(data2, store.get(id2));
        assertEquals(data1, store.remove(id1));
        assertNull(store.get(id1));
        assertEquals(data2, store.get(id2));
        assertEquals(data2, store.remove(id2));
        assertNull(store.get(id2));
    }
}
