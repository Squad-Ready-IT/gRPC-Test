package uk.startup.grpc.test.server.providers;

import org.junit.Before;
import org.junit.Test;
import uk.startup.grpc.test.server.exceptions.LoadDataException;
import uk.startup.grpc.test.server.readers.InputStreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class HttpDataProviderTest {

    private UrlConnectionProvider connectionProvider;
    private InputStreamReader<byte[]> reader;
    private DataProvider provider;


    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        connectionProvider = mock(UrlConnectionProvider.class);
        reader = (InputStreamReader<byte[]>) mock(InputStreamReader.class);
        provider = new HttpDataProvider(connectionProvider, reader);
    }


    @Test
    public void test_LoadData_Success() throws LoadDataException, IOException {
        URL testUrl = new URL("http://test.url");
        Map<String, List<String>> testResponseHeaders = new HashMap<String, List<String>>() {{
            put("header_1", Collections.singletonList("one"));
            put("header_2", Collections.singletonList("two"));
        }};
        byte[] testResponseBody = "Test Content".getBytes();

        URLConnection conn = mock(URLConnection.class);
        InputStream is = mock(InputStream.class);

        when(conn.getHeaderFields()).thenReturn(testResponseHeaders);
        when(conn.getInputStream()).thenReturn(is);
        when(connectionProvider.getConnection(eq(testUrl))).thenReturn(conn);

        when(reader.read(eq(is))).thenReturn(testResponseBody);

        ResourceData resourceData = provider.load(testUrl.toString());

        verify(connectionProvider, times(1)).getConnection(eq(testUrl));
        verify(conn, times(1)).getHeaderFields();
        verify(conn, times(1)).getInputStream();
        verify(reader, times(1)).read(eq(is));

        assertEquals(
                new HashMap<String, String>() {{
                    put("header_1", "one");
                    put("header_2", "two");
                }},
                resourceData.getHeaders()
        );
        assertEquals(testResponseBody, resourceData.getBody());
    }

    @Test
    public void test_LoadData_Success_EmptyData() throws LoadDataException, IOException {
        URL testUrl = new URL("http://test.url");
        Map<String, List<String>> testResponseHeaders = null;

        URLConnection conn = mock(URLConnection.class);
        InputStream is = mock(InputStream.class);

        when(conn.getHeaderFields()).thenReturn(testResponseHeaders);
        when(conn.getInputStream()).thenReturn(is);
        when(connectionProvider.getConnection(eq(testUrl))).thenReturn(conn);

        when(reader.read(eq(is))).thenReturn(null);

        ResourceData resourceData = provider.load(testUrl.toString());

        verify(connectionProvider, times(1)).getConnection(eq(testUrl));
        verify(conn, times(1)).getHeaderFields();
        verify(conn, times(1)).getInputStream();
        verify(reader, times(1)).read(eq(is));

        Map<String, String> expectedHeaders = Collections.emptyMap();
        byte[] expectedContent = new byte[0];

        assertEquals(expectedHeaders, resourceData.getHeaders());
        assertArrayEquals(expectedContent, resourceData.getBody());
    }

    @Test
    public void test_LoadData_Fail_InvalidUrl() {
        String testUrl = "test.url";

        try {
            provider.load(testUrl);
        } catch (LoadDataException exc) {
            assertTrue(exc.getCause() instanceof MalformedURLException);
        }
    }

    @Test
    public void test_LoadData_Fail_ConnectionIsNull() {
        String testUrl = "http://test.url";

        try {
            provider.load(testUrl);
        } catch (LoadDataException exc) {
            assertTrue(exc.getCause() instanceof NullPointerException);
        }
    }
}
