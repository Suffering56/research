package com.company.research;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author v.peschaniy
 *      Date: 27.02.2019
 */
public class PipelineExample {

    @SuppressWarnings("unchecked")
    static class Pipeline<InitialType, ResultType> {
        Stream<InitialType> initialStream;
        Class<?> nextCommandExpectedInputClass;
        Class<ResultType> resultClass;

        List<Command> commandsList = new ArrayList<>();
        boolean initialized = false;

        public Pipeline(Stream<InitialType> initialStream, Class<InitialType> initialClass, Class<ResultType> resultClass) {
            this.initialStream = initialStream;
            this.nextCommandExpectedInputClass = initialClass;
            this.resultClass = resultClass;
        }

        public <T, R> Pipeline<InitialType, ResultType> addMapCommand(Function<T, R> mapper, Class<T> inputClass, Class<R> resultClass) {
            validate(inputClass, resultClass);
            commandsList.add(new Command(mapper, CommandType.MAP));
            return this;
        }

        public <T, R> Pipeline<InitialType, ResultType> addFlatMapCommand(Function<T, Stream<R>> mapper, Class<T> inputClass, Class<R> resultClass) {
            validate(inputClass, resultClass);
            commandsList.add(new Command(mapper, CommandType.FLAT_MAP));
            return this;
        }

        public <T> Pipeline<InitialType, ResultType> addFilterCommand(Function<T, Boolean> predicate, Class<T> inputClass) {
            validate(inputClass, inputClass);
            commandsList.add(new Command(predicate, CommandType.FILTER));
            return this;
        }

        private <T, R> void validate(Class<T> inputClass, Class<R> resultClass) {
            Preconditions.checkState(nextCommandExpectedInputClass == inputClass, "input class[%s] does not match with expected class[%s]", inputClass, nextCommandExpectedInputClass);

            this.nextCommandExpectedInputClass = resultClass;
            initialized = this.resultClass == resultClass;
        }

        Stream<ResultType> resultStream() {
            Preconditions.checkState(initialized, "you need to add the last mapper into this pipeline");
            Stream stream = initialStream;

            for (Command command : commandsList) {
                switch (command.getType()) {
                    case MAP:
                        stream = stream.map(command);
                        break;
                    case FLAT_MAP:
                        stream = stream.flatMap(command);
                        break;
                    case FILTER:
                        stream = stream.filter(command::filter);
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
            return stream;
        }
    }

    enum CommandType {
        FILTER, MAP, FLAT_MAP
    }

    static class Command<T, R> implements Function<T, R> {

        Function<T, R> command;
        CommandType type;

        public Command(Function<T, R> command, CommandType type) {
            this.command = command;
            this.type = type;
        }

        @Override
        public R apply(T t) {
            return command.apply(t);
        }

        public Boolean filter(T t) {
            return (Boolean) command.apply(t);
        }

        public CommandType getType() {
            return type;
        }
    }
}
