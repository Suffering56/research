package com.company.research.experiments;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
public class Concurrency3 {

    @Test
    public void test1_1_waitNotify() throws InterruptedException {
        final int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger waitCounter = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            int threadIndex = i;
            executorService.submit(() -> {
                synchronized (this) {
                    slowFunction(0.1);
                    System.out.println("t[" + threadIndex + "]: beforeWait");
                    waitCounter.incrementAndGet();
                    wait();
                    System.out.println("t[" + threadIndex + "]: afterWait");
                }
                return null;
            });
        }
        executorService.shutdown();

        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {        //executorService.shutdown() + while(!executorService.awaitTermination() == join
            if (waitCounter.get() == threadCount) {
                synchronized (this) {
                    System.out.println("notify");
                    notify();
                }
            }
        }
        System.out.println("Main thread completed successfully!");
    }

    @Test
    public void test1_2_waitNotifyAll() throws InterruptedException {
        final int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger waitCounter = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            int threadIndex = i;
            executorService.submit(() -> {
                synchronized (this) {
                    slowFunction(0.1);
                    System.out.println("t[" + threadIndex + "]: beforeWait");
                    waitCounter.incrementAndGet();
                    wait();
                    System.out.println("t[" + threadIndex + "]: afterWait");
                }
                return null;
            });
        }
        executorService.shutdown();

        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {    //executorService.shutdown() + while(!executorService.awaitTermination() == join
            if (waitCounter.get() == threadCount) {
                synchronized (this) {
                    System.out.println("notifyAll");
                    notifyAll();
                    waitCounter.incrementAndGet();  //this saves you from reusing notifyAll
                }
            }
        }
        System.out.println("Main thread completed successfully!");
    }

    private long slowFunction() {
        return slowFunction(1.0);
    }

    private long slowFunction(double ratio) {
        long x = 1;
        long n = (long) (1000000000L + 5000000000L * Math.random() * ratio);
        for (long i = 2; i < n; i++) {
            x = x * i;
        }
        return x;
    }

    private void sleep(long sleepMillis, int sleepNanos) {
        try {
            Thread.sleep(sleepMillis, sleepNanos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
