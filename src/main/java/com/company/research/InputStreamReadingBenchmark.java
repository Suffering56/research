package com.company.research;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;

@SuppressWarnings("Duplicates")
@Warmup(iterations = 7)
@Fork(warmups = 1, value = 1)
@Measurement(iterations = 7)
@State(Scope.Benchmark)
public class InputStreamReadingBenchmark {

    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }

    @Param({"1", "64", "128", "256", "512", "1024", "2048", "4096", "8192"})
    public int bufferSize;

    @Benchmark
    public void x() {
//        System.out.println(state.a);
//        blackhole.consume(state.a);
    }
}
