package co.pragmati.kstreams.safe;

import co.pragmati.kstreams.safe.keyvalues.SafeKey;
import co.pragmati.kstreams.safe.keyvalues.SafeValue;
import co.pragmati.kstreams.safe.serdes.SafeKeySerde;
import co.pragmati.kstreams.safe.serdes.SafeValueSerde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.internals.ConsumedInternal;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;

public class SafeStreamsBuilder extends StreamsBuilder {

    public synchronized <K, V> SafeKStream<K, V> stream(String topic, SafeConsumed<K, V> safeConsumed) {
        Consumed<SafeKey<K>, SafeValue<V>> consumed = Consumed.with(SafeKeySerde.of(safeConsumed.keySerde), SafeValueSerde.of(safeConsumed.valueSerde), safeConsumed.timestampExtractor, safeConsumed.resetPolicy);
        return SafeKStreamImpl.of(super.stream((Collection)Collections.singleton(topic), consumed));
    }

    public synchronized <K, V> SafeKStream<K, V> stream(Collection<String> topics, SafeConsumed<K, V> safeConsumed) {
        Objects.requireNonNull(topics, "topics can't be null");
        Objects.requireNonNull(safeConsumed, "consumed can't be null");
        Consumed<SafeKey<K>, SafeValue<V>> consumed = Consumed.with(SafeKeySerde.of(safeConsumed.keySerde), SafeValueSerde.of(safeConsumed.valueSerde), safeConsumed.timestampExtractor, safeConsumed.resetPolicy);
        return SafeKStreamImpl.of(super.stream(topics, consumed));
    }

    public synchronized <K, V> SafeKStream<K, V> stream(Pattern topicPattern, SafeConsumed<K, V> safeConsumed) {
        Objects.requireNonNull(topicPattern, "topicPattern can't be null");
        Objects.requireNonNull(safeConsumed, "consumed can't be null");
        Consumed<SafeKey<K>, SafeValue<V>> consumed = Consumed.with(SafeKeySerde.of(safeConsumed.keySerde), SafeValueSerde.of(safeConsumed.valueSerde), safeConsumed.timestampExtractor, safeConsumed.resetPolicy);
        return SafeKStreamImpl.of(super.stream(topicPattern, consumed));
    }
}
