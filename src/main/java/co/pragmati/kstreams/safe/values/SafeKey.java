package co.pragmati.kstreams.safe.values;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SafeKey<T> {

    private byte[] source;
    private T key;
    private Optional<Exception> exception;

    public static SafeKey<byte[]> of(byte[] source) {
        return new SafeKey<>(source, source, Optional.empty());
    }

    public <U> SafeKey<U> map(Function<T, U> mapper) {
        try {
            return exception.map(this::<U>passThrough)
                    .orElseGet(() -> new SafeKey(source, mapper.apply(key), Optional.empty()));
        } catch (Exception t) {
            return passThrough(t);
        }
    }

    public boolean is(Predicate<T> predicate) {
        try {
            return exception.map(e -> false).orElseGet(() -> predicate.test(key));
        } catch (Exception t) {
            return false;
        }
    }

    <U> SafeKey<U> passThrough(final Exception t) {
        return new SafeKey<>(source, null, Optional.ofNullable(t));
    }

    public boolean failed() {
        return exception.isPresent();
    }

    public void logException() {
        exception.ifPresent(e -> log.error(e.getMessage(), e));
    }

}
