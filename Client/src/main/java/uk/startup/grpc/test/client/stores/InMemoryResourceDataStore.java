package uk.startup.grpc.test.client.stores;

import uk.startup.grpc.test.client.models.ResourceData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In memory resource data storage
 */
public class InMemoryResourceDataStore implements ResourceDataStore {

    private final Map<String, ResourceData> store = new ConcurrentHashMap<>();


    @Override
    public void add(String id, ResourceData data) {
        store.put(id, data);
    }

    @Override
    public ResourceData get(String id) {
        return store.get(id);
    }

    @Override
    public ResourceData remove(String id) {
        return store.remove(id);
    }
}
