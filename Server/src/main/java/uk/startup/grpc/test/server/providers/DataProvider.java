package uk.startup.grpc.test.server.providers;

import uk.startup.grpc.test.server.exceptions.LoadDataException;

public interface DataProvider {

    ResourceData load(String key) throws LoadDataException;
}
