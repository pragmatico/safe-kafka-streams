package co.pragmati.kstreams.safe.values;

import co.pragmati.kstreams.safe.SafeKStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.ValueMapperWithKey;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class  SafeKeyValue<K, V> {

    private final SafeKey<K> safeKey;
    private final SafeValue<V> safeValue;
    private final Optional<Exception> exception;

    public static <K, V> SafeKeyValue<K, V> pair(final SafeKey<K> safeKey, final SafeValue<V> safeValue) {
        Optional<Exception> optException = safeKey.getException().or(() -> safeValue.getException());
        return new SafeKeyValue<>(safeKey, safeValue, optException);
    }

    public <KR, VR> KeyValue<SafeKey<KR>, SafeValue<VR>> map(KeyValueMapper<? super K, ? super V, ? extends KeyValue<? extends KR, ? extends VR>> mapper) {
        try {
            return exception.map(this::<KR, VR>passThrough)
                    .orElseGet(() -> {
                        KeyValue<? extends KR, ? extends VR> mappedKeyValue = mapper.apply(safeKey.getKey(), safeValue.getValue());
                        return KeyValue.pair(safeKey.map(v -> mappedKeyValue.key), safeValue.map(v -> mappedKeyValue.value));
                    });
        } catch (Exception t) {
            return passThrough(t);
        }
    }

    public <KR> SafeKey<KR> selectKey(KeyValueMapper<? super K, ? super V, ? extends KR> mapper) {
        try {
            return exception.map(this::<KR, V>passThrough)
                    .orElseGet(() -> {
                        SafeKey<KR> newSafeKey = safeKey.map(k -> mapper.apply(safeKey.getKey(), safeValue.getValue()));
                        return KeyValue.pair(newSafeKey, safeValue);
                    }).key;
        } catch (Exception t) {
            return this.<KR, V>passThrough(t).key;
        }
    }

    public <VR> SafeValue<VR> mapValues(ValueMapperWithKey<? super K, ? super V, ? extends VR> mapper) {
        try {
            return exception.map(this::<K, VR>passThrough)
                    .orElseGet(() -> {
                        SafeValue<VR> newSafeValue = safeValue.map(k -> mapper.apply(safeKey.getKey(), safeValue.getValue()));
                        return KeyValue.pair(safeKey, newSafeValue);
                    }).value;
        } catch (Exception t) {
            return this.<K, VR>passThrough(t).value;
        }
    }

    public <KR, VR> Iterable<KeyValue<SafeKey<KR>, SafeValue<VR>>> flatMap(KeyValueMapper<? super K, ? super V, ? extends Iterable<? extends KeyValue<? extends KR, ? extends VR>>> mapper) {
        try {
            return exception.map(this::<KR, VR>passThrough).map(Arrays::asList)
                    .orElseGet(() -> {
                        Iterable<? extends KeyValue<? extends KR, ? extends VR>> flatMappedKeyValues = mapper.apply(safeKey.getKey(), safeValue.getValue());
                        List<KeyValue<SafeKey<KR>, SafeValue<VR>>> iterableKeyValues = Stream.generate(flatMappedKeyValues.iterator()::next)
                                .map(kv -> KeyValue.pair(safeKey.<KR>map(v -> kv.key), safeValue.<VR>map(v -> kv.value))).collect(toList());
                        return iterableKeyValues;
                    });
        } catch (Exception t) {
            return Arrays.asList(this.passThrough(t));
        }
    }

    public <VR> Iterable<SafeValue<VR>> flatMapValues(ValueMapperWithKey<? super K, ? super V, ? extends Iterable<? extends VR>> mapper) {
        try {
            return exception.map(this::<K, VR>passThrough).map(kv -> kv.value).map(Arrays::asList)
                    .orElseGet(() -> {
                        Iterable<? extends VR> flatMappedValues = mapper.apply(safeKey.getKey(), safeValue.getValue());
                        List<SafeValue<VR>> iterableValues = Stream.generate(flatMappedValues.iterator()::next)
                                .map(newValue -> safeValue.<VR>map(v -> newValue)).collect(toList());
                        return iterableValues;
                    });
        } catch (Exception t) {
            return Arrays.asList(this.<K, VR>passThrough(t).value);
        }
    }

    private <KR, VR> KeyValue<SafeKey<KR>, SafeValue<VR>> passThrough(final Exception t) {
        return KeyValue.pair(safeKey.passThrough(t), safeValue.passThrough(t));
    }

    /**
     * The evaluation of the predicate is defaulted to false if
     * - there has been an error in previous processing stages of the stream or,
     * - if there is an error when evaluating the predicate
     *
     * @param predicate
     * @return
     */
    public boolean is(Predicate<? super K, ? super V> predicate) {
        try {
            return exception.map(e -> false).orElseGet(() -> predicate.test(safeKey.getKey(), safeValue.getValue()));
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
