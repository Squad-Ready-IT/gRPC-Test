package uk.startup.grpc.test.server.readers;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class BytesReaderTest {

    private final BytesReader reader = new BytesReader();

    @Test
    public void test_Read_Success() throws IOException {
        InputStream is = new ByteArrayInputStream("Test".getBytes());
        byte[] bytes = reader.read(is);

        byte[] expectedBytes = "Test".getBytes();
        assertArrayEquals(expectedBytes, bytes);
    }

    @Test(expected = IOException.class)
    public void test_Read_Fail_IOException() throws IOException {
        InputStream is = mock(InputStream.class);

        when(is.available()).thenReturn(10);
        when(is.read()).thenThrow(IOException.class);
        when(is.read(any())).thenThrow(IOException.class);
        when(is.read(any(), anyInt(), anyInt())).thenThrow(IOException.class);

        reader.read(is);
    }
}
