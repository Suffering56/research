package com.company.research;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPOutputStream;

public class Utils {

    private static final AtomicLong counter = new AtomicLong();

    public static void printProgress(int iterations) {
        long c = counter.incrementAndGet();
        if (c % (iterations / 100) == 0) {
            int percent = (int) (c / (iterations / 100));
            System.out.println("progress: " + percent);
        }
    }

    public static byte[] gzip(byte[] dataToCompress) throws IOException {
//        byte[] dataToCompress = "This is the test data.".getBytes(StandardCharsets.ISO_8859_1);
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(dataToCompress.length);
             GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {

            zipStream.write(dataToCompress);
            zipStream.close();
            return byteStream.toByteArray();
        }
    }
}
