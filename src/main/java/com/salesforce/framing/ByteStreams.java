package com.salesforce.framing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public final class ByteStreams {

    private static ThreadLocal<byte[]> localBuffer = new ThreadLocal<byte[]>() {
        @Override
        public byte[] get() {
            final byte[] bytes = super.get();
            Arrays.fill(bytes, (byte)0);
            return bytes;
        }

        @Override
        protected byte[] initialValue() {
            return new byte[1024];
        }
    };

    public static void write(InputStream in, OutputStream out) throws IOException {
        try {
            final byte[] buffer = localBuffer.get();
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public static String toString(InputStream in) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = localBuffer.get();
            int nbytes;
            while ((nbytes = in.read(buffer)) > -1)
                out.write(buffer, 0, nbytes);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
