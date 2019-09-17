package com.company.research;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.lazy.primitive.CollectIntToObjectIterable;
import org.eclipse.collections.impl.lazy.primitive.FlatCollectIntToObjectIterable;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.*;
import java.util.function.IntSupplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author v.peschaniy
 *      Date: 16.09.2019
 */
@SuppressWarnings("Duplicates") @State(Scope.Benchmark)
@Warmup(iterations = 7)
@Fork(warmups = 1, value = 0b1)
@Measurement(iterations = 20)
public class JavaVsEclipseCollections {

//    @Param({"256", "512", "1024", "2048", "4096", "8192"})
//    private int bufferSize;

    private static NavigableMap<Integer, UnitScoreGroup> candidatesByUnitScoreMap = new TreeMap<>();
//    private static MutableSortedMap<Integer, UnitScoreGroup> candidatesByUnitScoreMap = new TreeSortedMap<>();
//    private static MutableSortedMap<Integer, UnitScoreGroupV2> candidatesByUnitScoreMap = new TreeSortedMap<>();


    private static final int NO_ENTRY_VALUE = -1;
    private static final int INITIAL_CAPACITY = 10;
    private static final IntSupplier randomIdSupplier = () -> random(1, 2_000_000);

    private static final MutableIntIntMap unitScoreByIdMap = new IntIntHashMap(INITIAL_CAPACITY);

    static {
        for (int i = 0; i < 1_000_000; i++) {
            int id = i * 2;
            MatchmakingCandidateWithUnitsScore candidate = randomCandidate(() -> id);
            addCandidate(candidate);

//            if (i < 100) {
//                System.out.println(candidate);
//            }
        }
    }

    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);

    }

    //    @Benchmark
    public void addCandidate(Blackhole blackhole) {
        boolean result = addCandidate(randomCandidate(randomIdSupplier));
        blackhole.consume(result);
    }

    //    @Benchmark
    public void removeCandidate(Blackhole blackhole) {
        int candidateId = randomIdSupplier.getAsInt();
        boolean result = removeCandidate(candidateId);
        blackhole.consume(result);
    }

    //    @Benchmark
    public void subMap(Blackhole blackhole) {
        int score1 = randomUnitScore();
        int score2 = randomUnitScore();

        Range<Integer> unitsScoreRange = Range.closed(
                Math.min(score1, score2),
                Math.max(score1, score2)
        );

        SortedMap<Integer, UnitScoreGroup> subMap = candidatesByUnitScoreMap.subMap(
                unitsScoreRange.lowerEndpoint(),
                unitsScoreRange.upperEndpoint()
        );

        int size = subMap.size();
        blackhole.consume(size);
    }

//    @Benchmark
//    public void searchJava(Blackhole blackhole) {
//        int score1 = randomUnitScore();
//        int score2 = randomUnitScore();
//
//        Range<Integer> unitsScoreRange = Range.closed(
//                Math.min(score1, score2),
//                Math.max(score1, score2)
//        );
//
//        List<Integer> ids = candidatesByUnitScoreMap.subMap(
//                unitsScoreRange.lowerEndpoint(),
//                unitsScoreRange.upperEndpoint()
//        )
//                .values().stream()
//                .flatMap(group -> StreamSupport.stream(
//                        Spliterators.spliteratorUnknownSize(group.candidates.keyValuesView().iterator(), Spliterator.ORDERED),
//                        false
//                ))
//                .filter(each -> each.getOne() != 50_000)
//                .filter(each -> each.getTwo() == 6)
//                .map(IntIntPair::getOne)
//                .collect(Collectors.toList());
//
//        if (!ids.isEmpty()) {
//            Integer outCandidate = ids.get(random(0, ids.size() - 1));
//            blackhole.consume(outCandidate);
//        }
//    }
//
//    @Benchmark
//    public void searchEclipse(Blackhole blackhole) {
//        int score1 = randomUnitScore();
//        int score2 = randomUnitScore();
//
//        Range<Integer> unitsScoreRange = Range.closed(
//                Math.min(score1, score2),
//                Math.max(score1, score2)
//        );
//
//        MutableList<Integer> ids = candidatesByUnitScoreMap.subMap(
//                unitsScoreRange.lowerEndpoint(),
//                unitsScoreRange.upperEndpoint()
//        )
//                .asLazy()
//                .flatCollect(group -> group.candidates.keyValuesView())
//                .select(each -> each.getOne() != 50_000)
//                .select(each -> each.getTwo() == 6)
//                .collect(IntIntPair::getOne)
//                .toList();
//
//        if (!ids.isEmpty()) {
//            Integer outCandidate = ids.get(random(0, ids.size() - 1));
//            blackhole.consume(outCandidate);
//        } else {
//            System.out.println("not found: " + unitsScoreRange);
//        }
//    }
//
////    @Benchmark
//    public void searchEclipseLikeJava(Blackhole blackhole) {
//        int score1 = randomUnitScore();
//        int score2 = randomUnitScore();
//
//        Range<Integer> unitsScoreRange = Range.closed(
//                Math.min(score1, score2),
//                Math.max(score1, score2)
//        );
//
//        List<Integer> ids = candidatesByUnitScoreMap.subMap(
//                unitsScoreRange.lowerEndpoint(),
//                unitsScoreRange.upperEndpoint()
//        )
//                .values().stream()
//                .flatMap(group -> StreamSupport.stream(
//                        Spliterators.spliteratorUnknownSize(group.candidates.keyValuesView().iterator(), Spliterator.ORDERED),
//                        false
//                ))
//                .filter(each -> each.getOne() != 50_000)
//                .filter(each -> each.getTwo() == 6)
//                .map(IntIntPair::getOne)
//                .collect(Collectors.toList());
//
//        if (!ids.isEmpty()) {
//            Integer outCandidate = ids.get(random(0, ids.size() - 1));
//            blackhole.consume(outCandidate);
//        }
//    }

    @Benchmark
    public void searchJavaShifting(Blackhole blackhole) {
        int score1 = randomUnitScore();
        int score2 = randomUnitScore();

        Range<Integer> unitsScoreRange = Range.closed(
                Math.min(score1, score2),
                Math.max(score1, score2)
        );

        IntList ids = candidatesByUnitScoreMap.subMap(
                unitsScoreRange.lowerEndpoint(),
                unitsScoreRange.upperEndpoint()
        )
                .values()
                .stream()
                .flatMapToInt(group -> group.candidates.values().stream()
                        .mapToInt(Integer::intValue)
                )
                .filter(key -> {
                    int id = key >> 4;
                    int unitsCount = key - (id << 4);
                    return id != 50_000 && unitsCount == 6;
                })
                .collect(
                        IntArrayList::new,
                        IntArrayList::add,
                        IntArrayList::addAll
                );

        if (!ids.isEmpty()) {
            Integer outCandidate = ids.get(random(0, ids.size() - 1));
            blackhole.consume(outCandidate);
        }
        else {
//            System.out.println("not found: " + unitsScoreRange);
        }
    }


    public static Collector<Integer, ?, IntArrayList> toEclipseIntList() {
        return Collector.of(
                IntArrayList::new,
                IntArrayList::add,
                (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                }
        );
    }

//    public void searchEclipseWithBitShifting(Blackhole blackhole) {
//        int score1 = randomUnitScore();
//        int score2 = randomUnitScore();
//
//        Range<Integer> unitsScoreRange = Range.closed(
//                Math.min(score1, score2),
//                Math.max(score1, score2)
//        );
//
//        MutableList<Integer> ids = candidatesByUnitScoreMap.subMap(
//                unitsScoreRange.lowerEndpoint(),
//                unitsScoreRange.upperEndpoint()
//        )
//                .flatCollect(group -> group.candidates)
//                .select(key -> {
//                    int id = key >> 4;
//                    int unitsCount = key - (id << 4);
//                    return id != 50_000 && unitsCount == 6;
//                })
//                .toList();
//
//        if (!ids.isEmpty()) {
//            Integer outCandidate = ids.get(random(0, ids.size() - 1));
//            blackhole.consume(outCandidate);
//        }
//        else {
////            System.out.println("not found: " + unitsScoreRange);
//        }
//    }


    private static boolean addCandidate(MatchmakingCandidateWithUnitsScore candidate) {
        int candidateUnitScore = unitScoreByIdMap.getIfAbsent(candidate.getId(), NO_ENTRY_VALUE);

        if (candidateUnitScore != NO_ENTRY_VALUE) {
            //такой кандидат уже есть в группе
            return false;
        }

        unitScoreByIdMap.put(candidate.getId(), candidate.getUnitsScore());

        return addInternalCandidate(candidate);
    }

    private static boolean addInternalCandidate(MatchmakingCandidateWithUnitsScore candidate) {
        UnitScoreGroup unitScoreGroup = candidatesByUnitScoreMap.computeIfAbsent(
                candidate.getUnitsScore(),
                unitScore -> new UnitScoreGroup()
        );

        return unitScoreGroup.addCandidate(candidate);
    }

    private static boolean removeCandidate(int candidateId) {
        int candidateUnitScore = unitScoreByIdMap.removeKeyIfAbsent(candidateId, NO_ENTRY_VALUE);
        if (candidateUnitScore == NO_ENTRY_VALUE) {
            return false;
        }

        UnitScoreGroup unitScoreGroup = candidatesByUnitScoreMap.get(candidateUnitScore);
        if (unitScoreGroup == null) {
            return false;
        }

        unitScoreGroup.removeCandidate(candidateId);
        return true;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class MatchmakingCandidateWithUnitsScore {
        private final int id;
        private final int unitsCount;
        private final int unitsScore;

        @Override
        public String toString() {
            return "MatchmakingCandidateWithUnitsScore{" +
                    "id=" + id +
                    ", unitsCount=" + unitsCount +
                    ", unitsScore=" + unitsScore +
                    '}';
        }
    }

    private static class UnitScoreGroupV2 {
        private MutableIntObjectMap<Integer> candidates;   //id -> unitsCount

        //
        private UnitScoreGroupV2() {
//            this.candidates = new IntIntHashMap();
            this.candidates = new IntObjectHashMap<>();
        }

//        public boolean addCandidate(MatchmakingCandidateWithUnitsScore candidate) {
//            if (candidates.containsKey(candidate.getId())) {
//                //TODO: не уверен в нужности этой проверки, особенно если учесть что никому не интересно добавился кандидат или нет
//                return false;
//            }
//
//            candidates.put(candidate.getId(), candidate.getUnitsCount());
//            return true;
//        }

        public boolean addCandidate(MatchmakingCandidateWithUnitsScore candidate) {
            int id = candidate.getId();

            if (candidates.containsKey(id)) {
                //TODO: не уверен в нужности этой проверки, особенно если учесть что никому не интересно добавился кандидат или нет
                return false;
            }

            int value = (id << 4) + candidate.getUnitsCount();

            candidates.put(id, value);
            return true;
        }

        public void removeCandidate(int candidateId) {
            candidates.remove(candidateId);
        }
    }


    private static class UnitScoreGroup {
        //        private MutableIntIntMap candidates;   //id -> unitsCount
        private MutableIntObjectMap<Integer> candidates;   //id -> unitsCount


        private UnitScoreGroup() {
//            this.candidates = new IntIntHashMap();
            this.candidates = new IntObjectHashMap<>();
        }

//        public boolean addCandidate(MatchmakingCandidateWithUnitsScore candidate) {
//            if (candidates.containsKey(candidate.getId())) {
//                //TODO: не уверен в нужности этой проверки, особенно если учесть что никому не интересно добавился кандидат или нет
//                return false;
//            }
//
//            candidates.put(candidate.getId(), candidate.getUnitsCount());
//            return true;
//        }

        public boolean addCandidate(MatchmakingCandidateWithUnitsScore candidate) {
            int id = candidate.getId();

            if (candidates.containsKey(id)) {
                //TODO: не уверен в нужности этой проверки, особенно если учесть что никому не интересно добавился кандидат или нет
                return false;
            }

            int value = (id << 4) + candidate.getUnitsCount();

            candidates.put(id, value);
            return true;
        }

        public void removeCandidate(int candidateId) {
            candidates.remove(candidateId);
        }
    }


//        System.out.println("Integer.MAX_VALUE = " + Integer.MAX_VALUE);
//        int id = 133_456_789;
//        int uc = 10;
//
//        int id4 = id << 4;
//        int key = uc + id4;
//
//        int id0 = key >> 4;
//        int uc0 = key - (id0 << 4);
//
//        System.out.println("id      = " + id);
//        System.out.println("id4     = " + id4);
//        System.out.println("uc      = " + uc);
//        System.out.println("key     = " + key);
//
//        System.out.println("uc0     = " + uc0);
//        System.out.println("\r\nid0     = " + id0);
//
//
//
//        System.out.println("\r\n__\r\n\r\n_id     = " + Integer.toBinaryString(id));
//        System.out.println("_id4    = " + Integer.toBinaryString(id4));
//        System.out.println("_uc     = " + Integer.toBinaryString(uc));
//        System.out.println("_key    = " + Integer.toBinaryString(key));
//
//
//        System.out.println("_uc0    = " + Integer.toBinaryString(uc0));
//
//        System.out.println("\r\n_id0    = " + Integer.toBinaryString(id0));

    private static int random(int from, int to) {
        return from + (int) (Math.random() * (to));
    }

    private static int randomUnitScore() {
        return random(1, 100);
    }

    private static int randomUnitsCount() {
        return random(1, 6);
    }

    private static MatchmakingCandidateWithUnitsScore randomCandidate(IntSupplier idSupplier) {
        return new MatchmakingCandidateWithUnitsScore(
                idSupplier.getAsInt(),
                randomUnitsCount(),
                randomUnitScore()
        );
    }
}
