package co.pragmati.kstreams.safe.containers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * Parameterised container/wrapper for the Value in the <Key, Value> pair of KStreams.
 * This monad represents the outcome of a transformation dealing with unwanted side-effects,
 * it carries the original value and the outcome value after the transformation.
 * In case of error, carries the exception that caused the transformation to fail.
 *
 * @param <T>
 */
@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValueContainer<T> {

    private byte[] source;
    private T value;
    private Optional<Exception> exception;

    public static ValueContainer<byte[]> of(byte[] source) {
        return new ValueContainer<>(source, source, Optional.empty());
    }

    public <U> ValueContainer<U> map(Function<T, U> mapper) {
        try {
            return exception.map(this::<U>passThrough)
                    .orElseGet(() -> new ValueContainer<>(source, mapper.apply(value), Optional.empty()));
        } catch (Exception t) {
            return passThrough(t);
        }
    }

    public boolean is(Predicate<T> predicate) {
        try {
            return exception.map(e -> false).orElseGet(() -> predicate.test(value));
        } catch (Exception t) {
            return false;
        }
    }

    private <U> ValueContainer<U> passThrough(final Exception t) {
        return new ValueContainer<>(source, null, Optional.ofNullable(t));
    }

    public boolean failed() {
        return exception.isPresent();
    }

    public void logException() {
        exception.ifPresent(e -> log.error(e.getMessage(), e));
    }

}