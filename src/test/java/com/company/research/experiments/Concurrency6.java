package com.company.research.experiments;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Concurrency6 {

    @Test
    public void test1_executorService() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(9);

        long start = System.currentTimeMillis();

        List<Callable<Integer>> tasksList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            int finalI1 = i;
            tasksList.add(() -> {
                Thread.sleep(3000);
//                return  finalI % 2 == 0 ? 1 : 0;
                return 1;
            });
        }

        System.out.println("before invokeAll");

        List<Future<Integer>> futures = executorService.invokeAll(tasksList);

        int sum = futures.stream()
                .mapToInt(integerFuture -> {
                    try {
                        return integerFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return 0;
                    }
                })
                .sum();

        System.out.println("sum = " + sum);

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        System.out.println("Main thread completed successfully!");
    }

    @Test
    public void test2_executorSeervice() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(50);

        List<Callable<Integer>> tasksList = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            int finalI = i;
            tasksList.add(() -> finalI);
        }

        System.out.println("before invokeAll");

        List<Future<Integer>> futures = executorService.invokeAll(tasksList);

        for (Future<Integer> future : futures) {
            System.out.println("future.get() = " + future.get());
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        System.out.println("Main thread completed successfully!");
    }

    @Test
    public void test3_cancelFutureSync() throws InterruptedException, ExecutionException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(50);

        ScheduledFuture<?> future = executorService.schedule(() -> {
            System.out.println("task: before");
            longCalculation();
            System.out.println("task: after");
            return 100;
        }, 1, TimeUnit.SECONDS);

        sleep(2000);
        System.out.println("cancel");
        future.cancel(false);

        try {
            System.out.println("get: before");
            Object o = future.get();
            System.out.println("o = " + o);
        }
        catch (CancellationException e) {
            System.out.println("CancellationException!");
        }
        System.out.println("get: after");


        System.out.println("shutdown");
        executorService.shutdown();
        System.out.println("await termination");
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        System.out.println("Main thread completed successfully!");
    }


    @Test
    public void test4_cancelFutureSync() throws InterruptedException, ExecutionException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(50);

        ScheduledFuture<?> future = executorService.schedule(() -> {
            System.out.println("task: before");
            longCalculation();
            System.out.println("task: after");
            return 100;
        }, 1, TimeUnit.SECONDS);

        System.out.println("state.before cancel: isDone=" + future.isDone() + ", isCancelled=" + future.isCancelled());

        sleep(2000);
        System.out.println("cancel");
        future.cancel(false);

        System.out.println("state.after cancel: isDone=" + future.isDone() + ", isCancelled=" + future.isCancelled());


        try {
            System.out.println("get: before");
            Object o = future.get();
            System.out.println("o = " + o);
        }
        catch (CancellationException e) {
            System.out.println("CancellationException!");
        }
        System.out.println("get: after");


        System.out.println("shutdown");
        executorService.shutdown();
        System.out.println("await termination");
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        System.out.println("Main thread completed successfully!");
    }

    private void longCalculation() {
        int x = 10;
        for (long i = 0; i < 9000000000L; i++) {
            if (i % 2 == 0) {
                x = x * 10 + 500;
            }
            else {
                x = x - 500;
                x = x / 10;
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
