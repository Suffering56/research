package com.company.research.experiments;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author v.peschaniy
 *      Date: 05.04.2019
 */
public class Concurrency7 {

    @Test
    public void test() throws InterruptedException, ExecutionException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(50);

        try {


            executorService.schedule(safeRunnable(
                    () -> task(1),
                    Throwable::printStackTrace
            ), 1, TimeUnit.SECONDS);

            sleep(2000);

            System.out.println("shutdown: " + executorService.isShutdown());
            executorService.shutdown();
            System.out.println("after shutdown: " + executorService.isShutdown());

            executorService.schedule(safeRunnable(
                    () -> task(2),
                    Throwable::printStackTrace
            ), 1, TimeUnit.SECONDS);

        }
        finally {
            System.out.println("await termination");
            while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

            System.out.println("Main thread completed!");
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

    private int task(int number) {
        System.out.println("task[" + number + "]: before");
        sleep(3000);
        System.out.println("task[" + number + "]: after");
        return 100;
    }


    interface ExceptionableRunnable {
        void run() throws Exception;
    }


    public static Runnable safeRunnable(ExceptionableRunnable runnable, Consumer<Exception> exceptionConsumer) {
        return () -> {
            try {
                runnable.run();
            }
            catch (Exception e) {
                if (exceptionConsumer != null) {
                    exceptionConsumer.accept(e);
                }
            }

        };
    }


    @Test
    public void testSleep() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(30);

        MutableInt i = new MutableInt();

        executorService.scheduleAtFixedRate(() -> {
            System.out.println("action[" + i.getValue() + "] started");
            sleep(3000);
            System.out.println("action[" + i.getValue() + "] finished");
            i.increment();
        }, 0, 1, TimeUnit.SECONDS);

        sleep(10000);
        executorService.shutdown();

        System.out.println("await termination");
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        System.out.println("Main thread completed!");
    }
}
