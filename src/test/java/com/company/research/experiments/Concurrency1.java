package com.company.research.experiments;

import com.company.research.ConcurrentUtils;
import com.company.research.exceptions.UnattainablePointException;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
public class Concurrency1 {

    @Test
    public void test1_1() throws ExecutionException, InterruptedException {
        AtomicInteger result = new AtomicInteger();
        FutureTask<?> task = createSimpleTask(result);

        new Thread(task).start();

        while (!task.isDone()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        System.out.println("result = " + result.get());

        assert result.get() == 10;
    }

    @Test
    public void test1_2() throws ExecutionException, InterruptedException {
        AtomicInteger result = new AtomicInteger();
        FutureTask<?> task = createSimpleTask(result);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(task);
        executorService.shutdown();

        while (!executorService.isTerminated()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        System.out.println("result = " + result.get());

        assert result.get() == 10;
    }

    private FutureTask<?> createSimpleTask(AtomicInteger result) {
        return new FutureTask<>(() -> {
            TimeUnit.SECONDS.sleep(2);
            result.set(10);
            return null;
        });
    }

    @Test
    public void test2_1_noSync() throws InterruptedException {
        executeInBackgroundThread(() -> {
            slowFunction();
            System.out.println("Курица!");
        }, 0, 1);

        executeInBackgroundThread(() -> {
            slowFunction();
            System.out.println("Яйцо!");
        }, 0, 1);

        ConcurrentUtils.sleepInfinity();
    }

    @Test
    public void test2_2_syncMethod() throws InterruptedException {
        executeInBackgroundThread(() -> {
            slowFunctionSync();
            System.out.println("Курица!");
        }, 0, 1);

        executeInBackgroundThread(() -> {
            slowFunctionSync();
            System.out.println("Яйцо!");
        }, 0, 1);

        ConcurrentUtils.sleepInfinity();
    }

    @Test
    public void test2_3_syncBlock() throws InterruptedException {
        executeInBackgroundThread(() -> {
            synchronized (this) {
                slowFunction();
            }
            System.out.println("Курица!");
        }, 0, 1);

        executeInBackgroundThread(() -> {
            synchronized (this) {
                slowFunction();
            }
            System.out.println("Яйцо!");
        }, 0, 1);

        ConcurrentUtils.sleepInfinity();
    }

    private long slowFunction() {
        long x = 1;
        long n = (long) (1000000000L + 5000000000L * Math.random());
        for (long i = 2; i < n; i++) {
            x = x * i;
        }
        return x;
    }

    private synchronized long slowFunctionSync() {
        return slowFunction();
    }

    @Test
    public void test3_1_readingIncorrect() throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            System.out.println("background thread started");
            while (!interruptedUnsafe) i++;
            System.out.println("background thread completed successfully: i=" + i);
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(3);
        interruptedUnsafe = true;
        System.out.println("isInterrupted = true");

        backgroundThread.join();
        throw new UnattainablePointException();
    }

    @Test
    public void test3_2_readingVolatile() throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            System.out.println("background thread started");
            while (!interruptedVolatile) i++;
            System.out.println("background thread completed successfully: i=" + i);
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(3);
        interruptedVolatile = true;
        System.out.println("isInterrupted = true");

        backgroundThread.join();
        System.out.println("main thread completed successfully!");
    }

    @Test
    public void test3_3_readingSync() throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            System.out.println("background thread started");
            while (!isInterruptedSync()) i++;
            System.out.println("background thread completed successfully: i=" + i);
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(3);
        setInterruptedSync(true);
        System.out.println("setInterrupted(true)");

        backgroundThread.join();
        System.out.println("main thread completed successfully!");
    }

    @Test
    public void test3_4_readingYield() throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            System.out.println("background thread started");
            while (!interruptedUnsafe) {
                i++;
                Thread.yield(); // ~~ TimeUnit.NANOSECONDS.sleep(1);
            }
            System.out.println("background thread completed successfully: i=" + i);
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(3);
        interruptedUnsafe = true;
        System.out.println("isInterrupted = true");

        backgroundThread.join();
        System.out.println("main thread completed successfully!");
    }

    private boolean interruptedUnsafe = false;
    private boolean interruptedSync = false;
    private volatile boolean interruptedVolatile = false;

    public boolean isInterruptedSync() {
        return interruptedSync;
    }

    public void setInterruptedSync(boolean interruptedSync) {
        this.interruptedSync = interruptedSync;
    }


    private Thread executeInBackgroundThread(Runnable runnable, long sleepMillis, int sleepNanos) {
        Thread thread = new Thread(() -> {
            while (true) {
                runnable.run();
                ConcurrentUtils.sleep(sleepMillis, sleepNanos);
            }
        });
        thread.start();

        return thread;
    }
}
