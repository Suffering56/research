package com.company.research.experiments;

import com.company.research.ConcurrentUtils;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("Duplicates")
public class Concurrency5 {

    /**
     * ScheduledExecutorService
     *
     * @throws InterruptedException
     */
    @Test
    public void test1_schedule() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        long start = System.currentTimeMillis();

        executorService.schedule(() -> {
            long estimated = System.currentTimeMillis() - start;
            System.out.println("estimated = " + estimated);
        }, 3, TimeUnit.SECONDS);


        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        System.out.println("Main thread completed successfully!");
    }

    @Test
    public void test2_scheduleAtFixedRate() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        AtomicInteger counter = new AtomicInteger(0);

        long start = System.currentTimeMillis();

        executorService.scheduleAtFixedRate(() -> {
            long estimated = System.currentTimeMillis() - start;
            System.out.println("\nestimated.before = " + estimated);
            ConcurrentUtils.slowFunction(1);

            estimated = System.currentTimeMillis() - start;
            System.out.println("estimated.after = " + estimated);


            if (counter.incrementAndGet() == 5) {
                executorService.shutdown();
            }
        }, 0, 5, TimeUnit.SECONDS);

        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        System.out.println("Main thread completed successfully!");
    }

    @Test
    public void test3_scheduleWithFixedDelay() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        AtomicInteger counter = new AtomicInteger(0);

        long start = System.currentTimeMillis();

        executorService.scheduleWithFixedDelay(() -> {
            long estimated = System.currentTimeMillis() - start;
            System.out.println("\nestimated.before = " + estimated);
            ConcurrentUtils.slowFunction(1);

            estimated = System.currentTimeMillis() - start;
            System.out.println("estimated.after = " + estimated);


            if (counter.incrementAndGet() == 5) {
                executorService.shutdown();
            }
        }, 0, 5, TimeUnit.SECONDS);

        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        System.out.println("Main thread completed successfully!");
    }

    @Test
    public void test4_scheduleWithFixedDelay_parallel() throws InterruptedException {
        final int threadsCount = 9;
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(threadsCount);
        AtomicInteger counter = new AtomicInteger(0);

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadsCount; i++) {
            int threadIndex = i;
            executorService.scheduleWithFixedDelay(() -> {
                long estimated = System.currentTimeMillis() - start;
                System.out.println(tab(threadIndex) + "estimated[" + threadIndex + "].before = " + estimated);
                ConcurrentUtils.slowFunction(1);

                estimated = System.currentTimeMillis() - start;
                System.out.println(tab(threadIndex) + "estimated[" + threadIndex + "].after = " + estimated);


                if (counter.incrementAndGet() == 5) {
                    System.out.println("=====================shutdown====================");
                    executorService.shutdown();
                }
            }, 0, 5, TimeUnit.SECONDS);
        }

        while (!executorService.awaitTermination(1, TimeUnit.MILLISECONDS)) ;

        System.out.println("Main thread completed successfully!");
    }

    private String tab(int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append("\t");
        }
        return result.toString();
    }
}
