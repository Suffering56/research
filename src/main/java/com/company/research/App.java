package com.company.research;

import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.*;

//@SpringBootApplication
@SuppressWarnings("Duplicates")
@Warmup(iterations = 7)
@Fork(warmups = 1, value = 1)
@Measurement(iterations = 7)
public class App {

    private static Map<Integer, Map<Integer, String>> mapOfMap = new HashMap<Integer, Map<Integer, String>>(8) {{
        for (int i = 0; i < 8; i++) {
            Map<Integer, String> row = new HashMap<>(8);
            for (int j = 0; j < 8; j++) {
                String value = "iii:" + i + "jjj:" + j;
                row.put(j, value);
            }
            put(i, row);
        }
    }};

    private static Map<Point, String> pointsMap = new HashMap<Point, String>(64) {{
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String value = "iii:" + i + "jjj:" + j;
                put(new Point(i, j), value);
            }
        }
    }};

    private static String[][] array = new String[8][8];

    private static List<List<String>> list = new ArrayList<List<String>>(8) {{
        for (int i = 0; i < 8; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                String value = "iii:" + i + "jjj:" + j;
                row.add(value);
            }
            add(row);
        }
    }};

    static {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String value = "iii:" + i + "jjj:" + j;
                array[i][j] = value;
            }
        }
    }

    static int iii = 3;
    static int jjj = 3;
    static Point p = new Point(3, 3);


//    @Benchmark
//    public String testMapOfMap() {
//        return mapOfMap.get(iii).get(jjj);
//    }
//
//    @Benchmark
//    public String testMapOfPoint() {
//        return pointsMap.get(p);
//    }
//
//    @Benchmark
//    public String testArray() {
//        return array[iii][jjj];
//    }
//
//    @Benchmark
//    public String testList() {
//        return list.get(iii).get(jjj);
//    }

//    @Benchmark
//    public Stream<String> listParallelStream() {
//        return list.parallelStream().flatMap(List::stream);
//    }
//
//    @Benchmark
//    public Stream<String> arrayParallelStream() {
//        return Arrays.stream(array).flatMap(Arrays::stream);
//    }

//    @Benchmark
//    public boolean testArg() {
//        return isCorrectIndexArg(5);
//    }
//
//    @Benchmark
//    public boolean testArgs() {
//        return isCorrectIndexArgs(5);
//    }
//
//
//    public static boolean isCorrectIndexArg(int index) {
//        return index >= 0 && index < 8;
//    }
//
//    public static boolean isCorrectIndexArgs(int... indexes) {
//        for (int index : indexes) {
//            if (index < 0 || index >= 8) {
//                return false;
//            }
//        }
//
//        return true;
//    }


//    @Benchmark
//    public int testBufferedInputStream262144() throws IOException {
//        return testBufferedInputStream(262144);
//    }

//    @Benchmark
//    public int testBufferedInputStream16384() throws IOException {
//        return testBufferedInputStream(16384);
//    }

    @Benchmark
    public int testBufferedInputStream08192() throws IOException {
        return testBufferedInputStream(8192);
    }

    @Benchmark
    public int testBufferedInputStream04096() throws IOException {
        return testBufferedInputStream(4096);
    }

    @Benchmark
    public int testBufferedInputStream02048() throws IOException {
        return testBufferedInputStream(2048);
    }

    @Benchmark
    public int testBufferedInputStream01024() throws IOException {
        return testBufferedInputStream(1024);
    }

    @Benchmark
    public int testBufferedInputStream00512() throws IOException {
        return testBufferedInputStream(512);
    }

    @Benchmark
    public int testBufferedInputStream00256() throws IOException {
        return testBufferedInputStream(256);
    }

    @Benchmark
    public int testBufferedInputStream00128() throws IOException {
        return testBufferedInputStream(128);
    }

    @Benchmark
    public int testBufferedInputStream00064() throws IOException {
        return testBufferedInputStream(64);
    }


    @Benchmark
    public int testChannelGzip08192() throws IOException {
        return testChannelGzip(8192);
    }

    @Benchmark
    public int testChannelGzip04096() throws IOException {
        return testChannelGzip(4096);
    }

    @Benchmark
    public int testChannelGzip02048() throws IOException {
        return testChannelGzip(2048);
    }

    @Benchmark
    public int testChannelGzip01024() throws IOException {
        return testChannelGzip(1024);
    }

    @Benchmark
    public int testChannelGzip00512() throws IOException {
        return testChannelGzip(512);
    }

    @Benchmark
    public int testChannelGzip00256() throws IOException {
        return testChannelGzip(256);
    }

    @Benchmark
    public int testChannelGzip00128() throws IOException {
        return testChannelGzip(128);
    }

    @Benchmark
    public int testChannelGzip00064() throws IOException {
        return testChannelGzip(64);
    }

    //    @Benchmark
//    public int testChannelGzip00001() throws IOException {
//        return testChannelGzip(1);
//    }


    @Benchmark
    public int readByBufferWithoutChannels8192() throws IOException {
        return readByBufferWithoutChannels(8192);
    }

    @Benchmark
    public int readByBufferWithoutChannels4096() throws IOException {
        return readByBufferWithoutChannels(4096);
    }

    @Benchmark
    public int readByBufferWithoutChannels2048() throws IOException {
        return readByBufferWithoutChannels(2048);
    }

    @Benchmark
    public int readByBufferWithoutChannels1024() throws IOException {
        return readByBufferWithoutChannels(1024);
    }

    @Benchmark
    public int readByBufferWithoutChannels0512() throws IOException {
        return readByBufferWithoutChannels(512);
    }

    @Benchmark
    public int readByBufferWithoutChannels0256() throws IOException {
        return readByBufferWithoutChannels(256);
    }

    @Benchmark
    public int readByBufferWithoutChannels0128() throws IOException {
        return readByBufferWithoutChannels(128);
    }

    @Benchmark
    public int readByBufferWithoutChannels0064() throws IOException {
        return readByBufferWithoutChannels(64);
    }


    public int testBufferedInputStream(int bufferSize) throws IOException {
        byte[] bytes = getBytesByCounter();

        InputStream in = new ByteArrayInputStream(bytes);
        try (BufferedInputStream stream = new BufferedInputStream(in, bufferSize)) {
            byte[] read = IOUtils.toByteArray(stream);
        }

        return 0;
    }

    public static int testChannel(int bufferSize) throws IOException {
        byte[] bytes = getBytesByCounter();

        try (InputStream is = new ByteArrayInputStream(bytes)) {
            ReadableByteChannel channel = Channels.newChannel(is);
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            byte[] read = new byte[is.available()];

            int offset = 0;
            while (channel.read(buffer) > 0) {
                buffer.flip();
                int length = buffer.remaining();
                buffer.get(read, offset, length);
                offset += length;
                buffer.clear();
            }
        }

        return 0;
    }


    private int testChannelGzip(int bufferSize) throws IOException {
        byte[] bytes = getBytesByCounter();


        try (InputStream in = new ByteArrayInputStream(bytes);
             ByteArrayOutputStream os = new ByteArrayOutputStream(256)) {

            ReadableByteChannel inChannel = Channels.newChannel(in);
            WritableByteChannel outChannel = Channels.newChannel(os);

            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            int read;
            while ((read = inChannel.read(buffer)) > 0) {
                buffer.rewind();
                buffer.limit(read);
                while (read > 0) {
                    read -= outChannel.write(buffer);
                }
                buffer.clear();
            }

            return os.toByteArray().length;
        }
    }


    private int readByBufferWithoutChannels(int bufferSize) throws IOException {
        byte[] bytes = getBytesByCounter();
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        try (InputStream in = new ByteArrayInputStream(bytes);
             ByteArrayOutputStream os = new ByteArrayOutputStream(256)) {

//            os.reset();

            int read;
            while ((read = in.read(buffer.array())) > 0) {
                buffer.rewind();
                buffer.limit(read);
                os.write(buffer.array());
                buffer.clear();
            }
            return os.toByteArray().length;
        }
    }


    private static byte[] getBytesByCounter() {
        return getRandomBytes();

//        BufferAndMemoryTest.counter++;
//        if (BufferAndMemoryTest.counter % 3 == 0) {
//            return BufferAndMemoryTest.bytes1;
//        } else if (BufferAndMemoryTest.counter % 3 == 1) {
//            return BufferAndMemoryTest.bytes2;
//        } else {
//            return BufferAndMemoryTest.bytes3;
//        }
    }


    //    private static String evaluationString = "64->3_1->16_66->1_516->5_645->1_70->1_75->1_77->1_20->3_161->1_674->3_100->1_102->1_553->1_174->1_559->5_178->1_50->9_51->16_567->2_55->10_631->1_56->12_187->1_59->1_61->1_62->1";
    private static String evaluationString = "0->1_1->17_642->1_516->9_645->1_522->4_780->1_657->1_20->1_24->1_672->3_161->1_674->3_165->1_553->1_171->1_684->1_686->1_174->1_559->5_560->3_561->6_690->1_178->1_50->11_51->18_52->43_180->1_567->2_55->11_56->18_57->2_58->2_187->1_59->2_60->5_61->4_62->3_63->2_576->1_192->1_64->7_65->1_66->1_836->1_70->1_71->9_75->1_76->1_589->1_717->3_77->1_78->2_207->5_848->1_213->1_94->1_734->1_478->1_735->1_96->2_97->11_738->1_100->1_485->1_614->1_230->19_102->1_103->4_493->2_884->1_885->1_631->1_633->1_123->1";
    private static Random random = new Random();
    private static Map<Integer, byte[]> bytesMap = RangeTest.generateBytesMap(evaluationString);


    private static byte[] getRandomBytes() {
        int index = random.nextInt(bytesMap.size());
        return bytesMap.get(index);
    }

    public static void main(String[] args) throws IOException, RunnerException, InterruptedException {
//        SpringApplication.run(App.class, args);
//        org.openjdk.jmh.Main.main(args);

//        System.out.println("bytesMap.size() = " + bytesMap.size());

        byte[] x = x(2048);
        System.out.println(new String(x));
    }

    private static byte[] x(int bufferSize) throws IOException {
//        byte[] bytes = getBytesByCounter();
        byte[] bytes = "Hello world".getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        try (InputStream in = new ByteArrayInputStream(bytes);
             ByteArrayOutputStream os = new ByteArrayOutputStream(256)) {

//            os.reset();

            int read;
            while ((read = in.read(buffer.array())) > 0) {
                buffer.rewind();
                buffer.limit(read);
                os.write(buffer.array());
                buffer.clear();
            }
            return os.toByteArray();
        }
    }



    @AllArgsConstructor
    static class Point {
        int row;
        int column;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return row == point.row &&
                    column == point.column;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column);
        }
    }
}
