package com.company.research;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BytesGenerator {

//    private static String evaluationString = "64->3_1->16_66->1_516->5_645->1_70->1_75->1_77->1_20->3_161->1_674->3_100->1_102->1_553->1_174->1_559->5_178->1_50->9_51->16_567->2_55->10_631->1_56->12_187->1_59->1_61->1_62->1";
//    private static String evaluationString = "0->1_1->17_642->1_516->9_645->1_522->4_780->1_657->1_20->1_24->1_672->3_161->1_674->3_165->1_553->1_171->1_684->1_686->1_174->1_559->5_560->3_561->6_690->1_178->1_50->11_51->18_52->43_180->1_567->2_55->11_56->18_57->2_58->2_187->1_59->2_60->5_61->4_62->3_63->2_576->1_192->1_64->7_65->1_66->1_836->1_70->1_71->9_75->1_76->1_589->1_717->3_77->1_78->2_207->5_848->1_213->1_94->1_734->1_478->1_735->1_96->2_97->11_738->1_100->1_485->1_614->1_230->19_102->1_103->4_493->2_884->1_885->1_631->1_633->1_123->1";
    private static String evaluationString = "1024->27_1025->8_1153->1_1158->2_1159->1_1160->1_777->1_11147->1_1041->1_20->1_24->1_11928->1_1183->2_161->1_165->1_169->4_170->1_172->2_33454->1_16303->1_1072->1_50->9_51->18_52->6_12341->1_55->1_56->10_57->28_58->4_59->21_60->122_61->1_62->11_63->1_192->1_64->8_9537->1_963->1_71->6_75->1_76->3_78->3_849->1_49362->1_93->2_734->1_94->2_95->1_96->4_97->2_995->1_13283->1_100->3_101->3_102->7_1002->1_54380->1_240->3_30705->1_1009->1_1138->1_883->2_1139->1_1140->3_1141->1_1144->1_1017->2_1018->2_763->2_1021->2_1023->1_16895->1";
    private static Random random = new Random();
    private static Random stringRandom = new Random();
    private static Map<Integer, byte[]> bytesMap = createBytesMap(evaluationString);

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(getRandomBytes().length);
        }
    }

    public static byte[] getRandomBytes() {
        int index = random.nextInt(bytesMap.size());
        return bytesMap.get(index);
    }

    private static Map<Integer, byte[]> createBytesMap(String evalStr) {
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

            Stream.iterate(offset, index -> index + 1)
                    .limit(frequency)
                    .forEach(index -> bytesMap.put(index, generateRandomBytes(bytesCount)));

            offset += frequency;
        }
        return bytesMap;
    }


    public static byte[] generateRandomBytes(int length) {
        final byte[] buffer = new byte[length];
        stringRandom.nextBytes(buffer);
        return buffer;
    }
}
