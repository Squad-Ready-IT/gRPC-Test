package uk.startup.grpc.test.server.readers;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamReader<R> {

    R read(InputStream is) throws IOException;
}
