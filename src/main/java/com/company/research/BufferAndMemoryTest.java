package com.company.research;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.runner.RunnerException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SuppressWarnings("Duplicates")
public class BufferAndMemoryTest {

    public static void main(String[] args) throws IOException, RunnerException, InterruptedException {
//        testBufferMemory();

        System.out.println("bytes1 = " + bytes1.length);
        System.out.println("bytes2 = " + bytes2.length);
        System.out.println("bytes3 = " + bytes3.length);

//        String str = str1;
//        InputStream in = new ByteArrayInputStream(str.getBytes());

//        byte[] read = testZ(in);

//        System.out.println(new String(read));
//        for (byte b : read) {
//            System.out.print(b);
//        }
    }

    private static byte[] testZ(InputStream in) throws IOException {
        try (BufferedInputStream stream = new BufferedInputStream(in, 4096)) {
            int b;
            while ((b = stream.read()) != -1) {
                System.out.print(b);
            }
        }

        return null;
    }

    private static byte[] testY(InputStream in) throws IOException {
        byte[] read;
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        try (InputStream is = new BufferedInputStream(in)) {
            ReadableByteChannel channel = Channels.newChannel(in);
            read = new byte[is.available()];

            int index = 0;
            while (channel.read(buffer) > 0) {
                buffer.flip();

                while (buffer.hasRemaining()) {
                    read[index] = buffer.get();
                    index++;
                }
                buffer.clear();
            }
        }
        return read;
    }

    private static byte[] testX(InputStream in) throws IOException {
        byte[] read;
        try (InputStream is = new BufferedInputStream(in)) {
            read = new byte[is.available()];
            while (is.read(read) != -1) {
                for (byte b : read) {
                    System.out.print(b);
                }
                System.out.println();
            }
            read = IOUtils.toByteArray(is);
        }


        return read;
    }

    private static void testBufferMemory() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long start = System.currentTimeMillis();

        ScheduledExecutorService systemThreadPool = Executors.newScheduledThreadPool(30);
        List<Callable<byte[]>> tasks = new ArrayList<>();
        for (int i = 0; i < 3 * 1000 * 1000; i++) {
            tasks.add(BufferAndMemoryTest::testBufferedInputStream);
//            tasks.add(BufferAndMemoryTest::testThreadLocalBuffer);
        }
        systemThreadPool.invokeAll(tasks);

        long estimated = System.currentTimeMillis() - start;
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("estimated: " + estimated);
        System.out.println("memory: " + (memoryAfter - memoryBefore));

        systemThreadPool.shutdown();
        Runtime.getRuntime().gc();
        scanner.nextLine();
    }


    private static byte[] testBufferedInputStream() throws IOException {
        byte[] bytes;
        if (counter % 2 == 1) {
            bytes = bytes1;
        } else {
            bytes = bytes2;
        }

        InputStream in = new ByteArrayInputStream(bytes);
        try (BufferedInputStream stream = new BufferedInputStream(in, 262144)) {
            byte[] read = IOUtils.toByteArray(stream);
        }

        counter++;
        return null;
    }


    private static final ThreadLocal<ByteBuffer> bufferSupplier = ThreadLocal.withInitial(() -> ByteBuffer.allocate(262144));

    private static byte[] testThreadLocalBuffer() throws IOException {
        byte[] bytes;
        if (counter % 2 == 1) {
            bytes = bytes1;
        } else {
            bytes = bytes2;
        }

        ReadableByteChannel channel;

//        byte[] read;

        try (InputStream is = new ByteArrayInputStream(bytes)) {
            channel = Channels.newChannel(is);
            ByteBuffer buffer = bufferSupplier.get();

//            ByteBuffer buffer = ByteBuffer.allocate(8192);

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

        counter++;
        return null;
    }


    public static final String str2396 = StringUtils.join("\n" +
            "2019-01-30 11:57:39\n" +
            "Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.181-b13 mixed mode):\n" +
            "\n" +
            "\"springScheduler-5\" #129 prio=5 os_prio=0 tid=0x0000000020b5e800 nid=0x2be0 waiting on condition [0x0000000041ede000]\n" +
            "   java.lang.Thread.State: WAITING (parking)\n" +
            "        at sun.misc.Unsafe.park(Native Method)\n" +
            "        - parking to wait for  <0x00000006c6fdc668> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)\n" +
            "        at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)\n" +
            "        at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)\n" +
            "        at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:1088)\n" +
            "        at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:809)\n" +
            "        at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)\n" +
            "        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
            "        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
            "        at java.lang.Thread.run(Thread.java:748)\n" +
            "\n" +
            "   Locked ownable synchronizers:\n" +
            "        - None\n" +
            "\n" +
            "\"springScheduler-4\" #128 prio=5 os_prio=0 tid=0x0000000020b55000 nid=0x3508 waiting on condition [0x0000000041ddf000]\n" +
            "   java.lang.Thread.State: WAITING (parking)\n" +
            "        at sun.misc.Unsafe.park(Native Method)\n" +
            "        - parking to wait for  <0x00000006c6fdc668> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)\n" +
            "        at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)\n" +
            "        at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)\n" +
            "        at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:1088)\n" +
            "        at java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:809)\n" +
            "        at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)\n" +
            "        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)\n" +
            "        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n" +
            "        at java.lang.Thread.run(Thread.java:748)\n" +
            "\n" +
            "   Locked ownable synchronizers:\n" +
            "        - None\n" +
            "\n"

    );

    public static final String str636 = StringUtils.join("\n" +
            "2019-01-30 11:57:39\n" +
            "Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.181-b13 mixed mode):\n" +
            "\n" +
            "\"springScheduler-5\" #129 prio=5 os_prio=0 tid=0x0000000020b5e800 nid=0x2be0 waiting on condition [0x0000000041ede000]\n" +
            "   java.lang.Thread.State: WAITING (parking)\n" +
            "        at sun.misc.Unsafe.park(Native Method)\n" +
            "        - parking to wait for  <0x00000006c6fdc668> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)\n" +
            "        at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)\n" +
            "        at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)\n"

    );


    public static String str34960 = StringUtils.join("asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw",
            "asdkasdkpqwjdpqjd qcmqpcmkpqcpqcp;qc,pq  q'pq dk'[qw dk'pqdk 'pqdk 'pqd 'pqkw'dpkq'dp qwdk 'pqw"
            );


    static byte[] bytes1 = str2396.getBytes();
    static byte[] bytes2 = str636.getBytes();
    static byte[] bytes3 = str34960.getBytes();


    static int counter = 0;
}
