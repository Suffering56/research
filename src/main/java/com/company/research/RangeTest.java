package com.company.research;

import java.io.BufferedInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RangeTest {


    public static void main(String[] args) {
//        Random random = new Random();
//
//        for (int i = 0; i < 1000; i++) {
//            int val = random.nextInt(100);
//            System.out.println("val = " + val);
//        }

//
//        generateBytesMap(evaluationString)
//                .forEach((index, bytes) -> System.out.println(index + "->" + arrayToString(bytes)));

    }

    private static String arrayToString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(b);
        }
        return result.toString();
    }

    public static Map<Integer, byte[]> generateBytesMap(String evalStr) {
        String[] split = evalStr.split("_");

        Map<Integer, Integer> parsedFrequencyMap = Arrays.stream(split).collect(Collectors.toMap(
                s -> Integer.parseInt(s.split("->")[0]),
                s -> Integer.parseInt(s.split("->")[1]))
        );

        Map<Integer, byte[]> bytesMap = new HashMap<>();

        int offset = 0;

        for (Integer key : parsedFrequencyMap.keySet()) {
            int bytesCount = key;
            int frequency = parsedFrequencyMap.get(key);
            byte[] bytes = generateRandomBytes(bytesCount);

            Stream.iterate(offset, index -> index + 1)
                    .limit(frequency)
                    .forEach(index -> bytesMap.put(index, bytes));

            offset += frequency;
        }
        return bytesMap;
    }

    private static Random stringRandom = new Random();

    private static byte[] generateRandomBytes(int length) {
        if (length == 1) {
            length = 1000 + (int) (10000 * Math.random());
        }
        final byte[] buffer = new byte[length];
        stringRandom.nextBytes(buffer);
        return buffer;
    }
}
