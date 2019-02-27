package com.company.research;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author v.peschaniy
 *      Date: 27.02.2019
 */
public class GameSettingsStreamWrapper {

    static class Wrapper<T> {
        @Getter private final T entity;
        @Getter private final String path;

        public Wrapper(T entity) {
            this.entity = entity;
            this.path = "";
        }

        public Wrapper(T entity, String path, Object... pathParams) {
            this.entity = entity;
            this.path = String.format(path, pathParams);
        }

        public <R> Stream<Wrapper<R>> stream(Function<T, Stream<R>> toStreamFunction, Function<R, String> pathMapper) {
            Preconditions.checkNotNull(entity);

            return toStreamFunction.apply(entity)
                    .map(r -> new Wrapper<>(r, path + "." + pathMapper.apply(r)));
        }

        public <R> Stream<Wrapper<R>> stream(Function<T, Stream<R>> toStreamFunction) {
            Preconditions.checkNotNull(entity);

            return toStreamFunction.apply(entity)
                    .map(r -> new Wrapper<>(r, path));
        }
    }

    public static void main(String[] args) {
        Stream.of(human1, human2)
                .map(human -> new Wrapper<>(human, "human[%s]", human.id))
                .flatMap(humanWrapper -> humanWrapper.stream(
                        human -> human.hands.stream(),
                        hand -> "hand[" + hand.id + "]"
                ))
                .flatMap(handWrapper -> handWrapper.stream(
                        hand -> hand.fingers.stream(),
                        finger -> "finger[" + finger.id + "]"
                ))
                .forEach(fingerWrapper -> System.out.println(fingerWrapper.path));

    }


    @RequiredArgsConstructor
    static class Human {
        private final int id;
        private List<Leg> legs = new ArrayList<>();
        private List<Hand> hands = new ArrayList<>();
    }

    @AllArgsConstructor
    static class Leg {
        private int id;
        private String position;
        private List<Finger> fingers;
    }

    @AllArgsConstructor
    static class Hand {
        private int id;
        private String position;
        private List<Finger> fingers;
    }

    @AllArgsConstructor
    static class Finger {
        private int id;
        private String name;
    }

    private static Human human1 = new Human(1);
    private static Human human2 = new Human(2);

    static {
        List<Finger> fingers = new ArrayList<>();
        fingers.add(new Finger(111, "thumb"));
        fingers.add(new Finger(222, "index"));
        fingers.add(new Finger(333, "middle"));
        fingers.add(new Finger(444, "ring"));
        fingers.add(new Finger(555, "little"));

        Leg rightLeg = new Leg(11, "right", fingers);
        Leg leftLeg = new Leg(22, "left", fingers);

        Hand rightHand = new Hand(11, "right", fingers);
        Hand leftHand = new Hand(22, "left", fingers);

        human1.legs.add(rightLeg);
        human1.legs.add(leftLeg);
        human1.hands.add(rightHand);
        human1.hands.add(leftHand);

        human2.legs.add(rightLeg);
        human2.legs.add(leftLeg);
        human2.hands.add(rightHand);
        human2.hands.add(leftHand);
    }
}


