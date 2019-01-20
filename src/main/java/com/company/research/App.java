package com.company.research;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//@SpringBootApplication
@Warmup(iterations = 5)
@Fork(warmups = 1, value = 1)
@Measurement(iterations = 7)
public class App {

    private static List<Integer> list = new ArrayList<Integer>() {{
        for (int i = 0; i < 100; i++) {
            add(i * 10 + 103);
        }
    }};

    public static void main(String[] args) throws IOException, RunnerException {
//        SpringApplication.run(App.class, args);
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public int test001() {
        return 0;
    }
}
