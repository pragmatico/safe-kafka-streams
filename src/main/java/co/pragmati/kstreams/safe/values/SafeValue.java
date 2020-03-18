package co.pragmati.kstreams.safe.values;

import co.pragmati.kstreams.safe.SafeKStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.kstream.ValueMapperWithKey;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Parameterised container/wrapper for the Value in the <Key, Value> pair of KStreams.
 * This monad represents the outcome of a transformation dealing with unwanted side-effects,
 * it carries the original value and the outcome value after the transformation.
 * In case of error, carries the exception that caused the transformation to fail.
 *
 * @param <V>
 */
@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SafeValue<V> {

    private byte[] source;
    private V value;
    private Optional<Exception> exception;

    public static SafeValue<byte[]> of(byte[] source) {
        return new SafeValue<>(source, source, Optional.empty());
    }
    
    public <VR> SafeValue<VR> map(ValueMapper<? super V, ? extends VR> mapper) {
        try {
            return exception.map(this::<VR>passThrough)
                    .orElseGet(() -> new SafeValue<>(source, mapper.apply(value), Optional.empty()));
        } catch (Exception t) {
            return passThrough(t);
        }
    }

    public <VR> Iterable<SafeValue<VR>> flatMapValues(ValueMapper<? super V, ? extends Iterable<? extends VR>> mapper) {
        try {
            return exception.map(this::<VR>passThrough).map(Arrays::asList)
                    .orElseGet(() -> {
                        Iterable<? extends VR> flatMappedValues = mapper.apply(value);
                        List<SafeValue<VR>> listOfValues = Stream.generate(flatMappedValues.iterator()::next)
                                .map(v -> new SafeValue<VR>(source, v, Optional.empty())).collect(Collectors.toList());
                        return listOfValues;
                    });
        } catch (Exception t) {
            return Arrays.asList(passThrough(t));
        }
    }

    <VR> SafeValue<VR> passThrough(final Exception t) {
        return new SafeValue<>(source, null, Optional.ofNullable(t));
    }

    public boolean is(Predicate<V> predicate) {
        try {
            return exception.map(e -> false).orElseGet(() -> predicate.test(value));
        } catch (Exception t) {
            return false;
        }
    }

    public boolean failed() {
        return exception.isPresent();
    }

    public void logException() {
        exception.ifPresent(e -> log.error(e.getMessage(), e));
    }

}