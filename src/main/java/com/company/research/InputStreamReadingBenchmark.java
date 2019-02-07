package com.company.research;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import javafx.beans.value.ObservableBooleanValue;
import lombok.Value;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import static com.company.research.BytesGenerator.getRandomBytes;

@SuppressWarnings("Duplicates")
@State(Scope.Benchmark)
//@Warmup(iterations = 15)
@Fork(warmups = 1, value = 1)
//@Measurement(iterations = 15)
public class InputStreamReadingBenchmark {

//    @Param({"1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096", "8192", "16384"})
    @Param({"256", "512", "1024", "2048", "4096", "8192"})
    private int bufferSize;

    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }

//    @Benchmark
//    public void testReadByIOUtilsToByteArray(InputStreamReadingBenchmark state, Blackhole blackhole) throws IOException {
//        String result = readByIOUtilsToByteArray(state.bufferSize);
//        blackhole.consume(result);
//    }

    @Benchmark
    public void testReadByAssistanceData(InputStreamReadingBenchmark state, Blackhole blackhole) throws IOException {
        String result = readByAssistantData(state.bufferSize);
        blackhole.consume(result);
    }

//
//    @Benchmark
//    public void testReadByChannel(InputStreamReadingBenchmark state, Blackhole blackhole) throws IOException {
//        String result = readByChannel(state.bufferSize, state.bufferSize);
//        blackhole.consume(result);
//    }
//
//    @Benchmark
//    public void testReadByAssistanceData(InputStreamReadingBenchmark state, Blackhole blackhole) throws IOException {
//        String result = readByAssistantData(state.bufferSize);
//        blackhole.consume(result);
//    }

//    @Benchmark
//    public void testReadByChannel256(InputStreamReadingBenchmark state, Blackhole blackhole) throws IOException {
//        String result = readByChannel(state.bufferSize, 256);
//        blackhole.consume(result);
//    }



    private String readByIOUtilsToByteArray(int bufferSize) throws IOException {

        InputStream in = new ByteArrayInputStream(getRandomBytes());
        try (BufferedInputStream stream = new BufferedInputStream(in, bufferSize)) {
            return new String(IOUtils.toByteArray(stream));
        }
    }

    private byte[] readByChannelIncorrect(int bufferSize) throws IOException {

        try (InputStream in = new ByteArrayInputStream(getRandomBytes());
             ReadableByteChannel channel = Channels.newChannel(in)) {

            int offset = 0;
            byte[] result = new byte[in.available()];
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            while (channel.read(buffer) > 0) {
                buffer.flip();
                int length = buffer.remaining();
                buffer.get(result, offset, length);
                offset += length;
                buffer.clear();
            }

            return result;
        }
    }

    private String readByChannel(int bufferSize, int outBufferSize) throws IOException {

        try (InputStream in = new ByteArrayInputStream(getRandomBytes());
             ByteArrayOutputStream out = new ByteArrayOutputStream(outBufferSize);
             ReadableByteChannel inChannel = Channels.newChannel(in);
             WritableByteChannel outChannel = Channels.newChannel(out)) {

            int read;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            while ((read = inChannel.read(buffer)) > 0) {
                buffer.rewind();
                buffer.limit(read);
                while (read > 0) {
                    read -= outChannel.write(buffer);
                }
                buffer.clear();
            }

            return new String(out.toByteArray());
        }
    }

    private String readByBufferIncorrect(int bufferSize, int outBufferSize) throws IOException {

        try (InputStream in = new ByteArrayInputStream(getRandomBytes());
             ByteArrayOutputStream os = new ByteArrayOutputStream(outBufferSize)) {     //TODO: os можно reset-ать, и переиспользовать

            int read;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);                        //TODO: нужно брать из ThreadLocal-а

            while ((read = in.read(buffer.array())) > 0) {
                buffer.rewind();
                buffer.limit(read);
                os.write(buffer.array());
                buffer.clear();
            }
            return new String(os.toByteArray());
        }
    }

    private static String readByAssistantData(int bufferSize) throws IOException {
        return readByAssistance(assistantMap.get(bufferSize));
    }

    private static String readByAssistance(ThreadLocal<AssistantData> assistantThreadLocalData) throws IOException {
        try (InputStream in = new ByteArrayInputStream(getRandomBytes())) {

            AssistantData assistantData = assistantThreadLocalData.get();
            ExtendedByteArrayOutputStream os = assistantData.getOutputStream();
            byte[] buffer = assistantData.getBuffer();

            int len;
            int totalLen = 0;
            while ((len = in.read(buffer)) > -1) {
                os.write(buffer, 0, len);
                totalLen += len;
            }

            return new String(os.getDataRef(), 0, totalLen);
        }
    }

    private static IntObjectMap<ThreadLocal<AssistantData>> assistantMap = createAssistantMap();

    private static IntObjectMap<ThreadLocal<AssistantData>> createAssistantMap() {
        int[] bufferSizes = new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384};

        IntObjectMap<ThreadLocal<AssistantData>> map = new IntObjectHashMap<>();

        for (int bufferSize : bufferSizes) {
            map.put(bufferSize, ThreadLocal.withInitial(() -> new AssistantData(
                    new byte[bufferSize],
                    new ExtendedByteArrayOutputStream(65536)
            )));
        }
        return map;
    }

    @Value
    private static class AssistantData {
        byte[] buffer;
        ExtendedByteArrayOutputStream outputStream;

        /**
         * Так как этот стрим будет переиспользоваться многократно, нужно обеспечить корректность
         * его состояния. А по сему - ресетим стрим перед тем как отдать.
         */
        public ExtendedByteArrayOutputStream getOutputStream() {
            outputStream.reset();
            return outputStream;
        }
    }
}
