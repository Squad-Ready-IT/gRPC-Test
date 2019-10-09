package uk.startup.grpc.test.client.stores;

import uk.startup.grpc.test.client.models.ResourceData;

/**
 * Storage for resource data
 */
public interface ResourceDataStore {

    /**
     * Adds new resource data associated with given id
     * @param id request id
     * @param data resource data
     */
    void add(String id, ResourceData data);

    /**
     * Gets resource data by id
     * @param id resource data id
     * @return resource data associated with given id
     */
    ResourceData get(String id);

    /**
     * Removes resource data by given id
     * @param id resource data id
     * @return removed resource data
     */
    ResourceData remove(String id);
}
