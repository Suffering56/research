package com.company.research.experiments;

import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("Duplicates")
public class Concurrency5 {

    @Test
    public void countDownLatchTest() throws InterruptedException {
        final int count = 3;
        CountDownLatch latch = new CountDownLatch(count);
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            System.out.println("before await");
            latch.await();  //can be use outside of synchronized block
            System.out.println("after await");
            return null;
        });

        while (latch.getCount() > 0) {
            TimeUnit.SECONDS.sleep(2);
            if (latch.getCount() == 1) {
                System.out.println("it's a fiiiiiiiiiiiinal countDown()");
            } else {
                System.out.println("countDown()");
            }
            latch.countDown();
        }
        System.out.println("Main thread successfully completed!");


    }

    @Test
    public void semaphoreTest() throws InterruptedException {
        final int threadsCount = 7;
        Semaphore semaphore = new Semaphore(3, true);
        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            final int threadIndex = i;
            executorService.submit(() -> {
                System.out.println("[" + threadIndex + "]: before acquire");
                semaphore.acquire();
                System.out.println("[" + threadIndex + "]: after acquire");
                return null;
            });
        }


        executorService.shutdown();
        int counter = 0;
        while (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
            counter++;
            System.out.println("semaphore.release: " + counter);
            semaphore.release(2);
        }


        System.out.println("Main thread successfully completed!");
    }

    @Test
    public void reentrantLockTest() throws InterruptedException {
        final int threadsCount = 7;
        Lock locker = new ReentrantLock();
        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            final int threadIndex = i;
            executorService.submit(() -> {
                try {
                    System.out.println("[" + threadIndex + "]: before lock");
                    locker.lock();
                    System.out.println("[" + threadIndex + "]: after lock");
                    Thread.sleep(1000);
                    System.out.println("[" + threadIndex + "]: locker.unlock ");
                } finally {
                    locker.unlock();
                }
                return null;
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(2, TimeUnit.SECONDS)) ;

        System.out.println("Main thread successfully completed!");
    }

    @Test
    public void x() {
        System.out.println(Integer.MAX_VALUE);
    }
}
