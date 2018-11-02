package com.company.research.experiments;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"Duplicates", "NonAtomicOperationOnVolatileField"})
public class Concurrency2 {

    private int unsafeX = 0;
    private volatile int volatileX = 0;
    private int synchronizedX = 0;
    private AtomicInteger atomicX = new AtomicInteger(0);

    /**
     * Наглядно показывается неатомарность оператора инкремента.
     * ExecutorService.submit;
     */
    @Test
    public void test1_1() {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Queue<FutureTask> tasksQueue = new ArrayDeque<>();

        for (int i = 0; i < 10; i++) {
            int taskIndex = i;
            FutureTask<Object> task = new FutureTask<>(() -> {
                for (int j = 0; j < 500; j++) {
                    unsafeIncrement();
                    volatileIncrement();
                    synchronizedIncrement();
                    atomicIncrement();
                    TimeUnit.MILLISECONDS.sleep(5);
                }
                System.out.println("task[" + taskIndex + "] was done!");
                return taskIndex;
            });

            tasksQueue.add(task);
            pool.submit(task);
        }

        FutureTask task;
        while ((task = tasksQueue.peek()) != null) {
            if (task.isDone()) {
                tasksQueue.poll();
            }
        }

        pool.shutdown();

        System.out.println("\nunsafeX = " + unsafeX);
        System.out.println("volatileX = " + volatileX);
        System.out.println("synchronizedX = " + synchronizedX);
        System.out.println("atomicX = " + atomicX.get());

        assert synchronizedX == 5000;
        assert atomicX.get() == 5000;

        System.out.println("main thread was completed successfully!");
    }


    /**
     * ExecutorService.invokeAll;
     */
    @Test
    public void test1_2() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        List<Callable<Integer>> tasksList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int taskIndex = i;

            tasksList.add(() -> {
                for (int j = 0; j < 500; j++) {
                    unsafeIncrement();
                    volatileIncrement();
                    synchronizedIncrement();
                    atomicIncrement();
                    TimeUnit.MILLISECONDS.sleep(5);
                }
                System.out.println("task[" + taskIndex + "] was done!");
                return taskIndex;
            });
        }

        System.out.println("invokeAll.before");
        pool.invokeAll(tasksList);              //==join
        System.out.println("invokeAll.after");
        pool.shutdown();

        System.out.println("\nunsafeX = " + unsafeX);
        System.out.println("volatileX = " + volatileX);
        System.out.println("synchronizedX = " + synchronizedX);
        System.out.println("atomicX = " + atomicX.get());

        assert synchronizedX == 5000;
        assert atomicX.get() == 5000;

        System.out.println("main thread was completed successfully!");
    }

    private void unsafeIncrement() {
        unsafeX++;
    }

    private void volatileIncrement() {
        volatileX++;
    }

    private synchronized void synchronizedIncrement() {
        synchronizedX++;
    }

    private void atomicIncrement() {
        atomicX.incrementAndGet();
    }
}
