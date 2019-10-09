package uk.startup.grpc.test.server.providers;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public interface UrlConnectionProvider {

    URLConnection getConnection(URL url) throws IOException;
}
