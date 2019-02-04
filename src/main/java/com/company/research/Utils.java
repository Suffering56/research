package com.company.research;

import java.util.concurrent.atomic.AtomicLong;

public class Utils {

    private static final AtomicLong counter = new AtomicLong();

    public static void printProgress(int iterations) {
        long c = counter.incrementAndGet();
        if (c % (iterations / 100) == 0) {
            int percent = (int) (c / (iterations / 100));
            System.out.println("progress: " + percent);
        }
    }
}
