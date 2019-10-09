package uk.startup.grpc.test.server.readers;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BytesReader implements InputStreamReader<byte[]> {

    @Override
    public byte[] read(InputStream is) throws IOException {
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            int result = bis.read();
            while(result != -1) {
                buf.write((byte) result);
                result = bis.read();
            }

            return buf.toByteArray();
        } finally {
            is.close();
        }
    }
}
