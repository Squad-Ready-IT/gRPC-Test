package uk.startup.grpc.test.server.providers;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class DefaultUrlConnectionProvider implements UrlConnectionProvider {

    @Override
    public URLConnection getConnection(URL url) throws IOException {
        return url.openConnection();
    }
}
