package uk.startup.grpc.test.server.providers;


import uk.startup.grpc.test.server.exceptions.LoadDataException;
import uk.startup.grpc.test.server.readers.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpDataProvider implements DataProvider {

    private UrlConnectionProvider connectionProvider;
    private InputStreamReader<byte[]> reader;

    public HttpDataProvider(
            UrlConnectionProvider connectionProvider,
            InputStreamReader<byte[]> reader
    ) {
        this.connectionProvider = connectionProvider;
        this.reader = reader;
    }

    @Override
    public ResourceData load(String resourceKey) throws LoadDataException {
        try {
            URL resourceUrl = new URL(resourceKey);
            URLConnection conn = connectionProvider.getConnection(resourceUrl);

            Map<String, String> headers = new HashMap<>();
            Optional.ofNullable(conn.getHeaderFields())
                    .orElse(Collections.emptyMap())
                    .forEach((key, value) -> {
                        if (key != null) {
                            String values = value.stream()
                                    .map(String::toString)
                                    .collect(Collectors.joining(","));
                            headers.put(key, values);
                        }
                    });

            byte[] body = Optional.ofNullable(reader.read(conn.getInputStream()))
                    .orElse(new byte[0]);

            return new ResourceData(headers, body);
        } catch (MalformedURLException exc) {
            throw new LoadDataException("Invalid URL - '" + resourceKey + "'", exc);
        } catch (Exception exc) {
            throw new LoadDataException(
                    String.format(
                            "Couldn't load data from URL '%s': %s",
                            resourceKey,
                            exc.getMessage()
                    ),
                    exc
            );
        }
    }
}
