package com.company.research;

import lombok.AllArgsConstructor;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.runner.RunnerException;

import java.io.*;
import java.util.*;

//@SpringBootApplication
//@SuppressWarnings("Duplicates")
//@Warmup(iterations = 7)
//@Fork(warmups = 1, value = 1)
//@Measurement(iterations = 7)
public class App {

    private static Map<Integer, Map<Integer, String>> mapOfMap = new HashMap<Integer, Map<Integer, String>>(8) {{
        for (int i = 0; i < 8; i++) {
            Map<Integer, String> row = new HashMap<>(8);
            for (int j = 0; j < 8; j++) {
                String value = "iii:" + i + "jjj:" + j;
                row.put(j, value);
            }
            put(i, row);
        }
    }};

    private static Map<Point, String> pointsMap = new HashMap<Point, String>(64) {{
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String value = "iii:" + i + "jjj:" + j;
                put(new Point(i, j), value);
            }
        }
    }};

    private static String[][] array = new String[8][8];

    private static List<List<String>> list = new ArrayList<List<String>>(8) {{
        for (int i = 0; i < 8; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                String value = "iii:" + i + "jjj:" + j;
                row.add(value);
            }
            add(row);
        }
    }};

    static {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String value = "iii:" + i + "jjj:" + j;
                array[i][j] = value;
            }
        }
    }

    static int iii = 3;
    static int jjj = 3;
    static Point p = new Point(3, 3);


//    @Benchmark
//    public String testMapOfMap() {
//        return mapOfMap.get(iii).get(jjj);
//    }
//
//    @Benchmark
//    public String testMapOfPoint() {
//        return pointsMap.get(p);
//    }
//
//    @Benchmark
//    public String testArray() {
//        return array[iii][jjj];
//    }
//
//    @Benchmark
//    public String testList() {
//        return list.get(iii).get(jjj);
//    }

//    @Benchmark
//    public Stream<String> listParallelStream() {
//        return list.parallelStream().flatMap(List::stream);
//    }
//
//    @Benchmark
//    public Stream<String> arrayParallelStream() {
//        return Arrays.stream(array).flatMap(Arrays::stream);
//    }

//    @Benchmark
//    public boolean testArg() {
//        return isCorrectIndexArg(5);
//    }
//
//    @Benchmark
//    public boolean testArgs() {
//        return isCorrectIndexArgs(5);
//    }
//
//
//    public static boolean isCorrectIndexArg(int index) {
//        return index >= 0 && index < 8;
//    }
//
//    public static boolean isCorrectIndexArgs(int... indexes) {
//        for (int index : indexes) {
//            if (index < 0 || index >= 8) {
//                return false;
//            }
//        }
//
//        return true;
//    }


    public static void main(String[] args) throws IOException, RunnerException, InterruptedException {
//        SpringApplication.run(App.class, args);
//        org.openjdk.jmh.Main.main(args);

        try (InputStream in = null) {

        }

        System.out.println("after");
    }


    @AllArgsConstructor
    static class Point {
        int row;
        int column;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return row == point.row &&
                    column == point.column;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column);
        }
    }
}
