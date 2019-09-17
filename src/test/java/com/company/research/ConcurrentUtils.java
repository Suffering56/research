package com.company.research;

import java.util.concurrent.TimeUnit;

public class ConcurrentUtils {

    public static void sleepInfinity() throws InterruptedException {
        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public static void sleep(long sleepMillis, int sleepNanos) {
        try {
            Thread.sleep(sleepMillis, sleepNanos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(long sleepMillis) {
        sleep(sleepMillis, 0);
    }

    public static long slowFunction(double ratio) {
        long x = 1;
        long n = (long) (1000000000L + 5000000000L * Math.random() * ratio);
        for (long i = 2; i < n; i++) {
            x = x * i;
        }
        return x;
    }
}
