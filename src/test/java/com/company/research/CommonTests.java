package com.company.research;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("ALL")
public class CommonTests {

    @Test
    public void test() {
        String x = "123";
        String y = "12".concat("3");

        boolean result = x == y;
        boolean internResult = x.intern() == y.intern();

        System.out.println("result = " + result);
        System.out.println("internResult = " + internResult);

        assert result == false;
        assert internResult == true;
    }


    private int[] arr1 = new int[]{5, 2, 8, 1, 3, 0, 9};
    private int[] arr2 = new int[]{3, 7, 123, 2, 3, 22, 24};

    @Test
    public void test2() {
        int[] result = new int[arr1.length + arr2.length];

        int index1 = 0;
        int index2 = 0;

        for (int i = 0; i < result.length; i++) {
            int minIndex1 = getMinIndex(arr1);
            int minIndex2 = getMinIndex(arr2);
            int min1, min2;

            if (minIndex1 == -1 && minIndex2 == -1) {
                break;
            }

            min1 = arr1[minIndex1];
            min2 = arr2[minIndex2];

            if (min1 < min2) {
                result[i] = min1;
                arr1[minIndex1] = Integer.MAX_VALUE;
            } else {
                result[i] = min2;
                arr2[minIndex2] = Integer.MAX_VALUE;
            }
        }

        Arrays.stream(result).forEach(System.out::println);
    }

    private int getMinIndex(int[] arr) {
        int minIndex = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[minIndex]) {
                minIndex = i;
            }
        }

        if (minIndex == 0 && arr[0] == Integer.MIN_VALUE) {
            return -1;
        }

        return minIndex;
    }

    @Test
    public void testStreamIterator() {
        List<Integer> list = Lists.newArrayList(1,2,3,4,5,6);
        Stream<Integer> stream = list.stream();

        Iterator<Integer> iterator = list.stream().iterator();
        Iterator<Integer> iterator2 = list.stream().iterator();
    }


}
