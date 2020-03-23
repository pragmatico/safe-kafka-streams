package co.pragmati.kstreams.safe;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.TimestampExtractor;

import static org.apache.kafka.streams.Topology.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SafeConsumed<K, V> {

    protected Serde<K> keySerde;
    protected Serde<V> valueSerde;
    protected TimestampExtractor timestampExtractor;
    protected AutoOffsetReset resetPolicy;
    protected String processorName;

    public static <K, V> SafeConsumed<K, V> with(final Serde<K> keySerde, final Serde<V> valueSerde) {
        return new SafeConsumed<>(keySerde, valueSerde, null, null, null);
    }

    public static <K, V> SafeConsumed<K, V> with(final Serde<K> keySerde, final Serde<V> valueSerde, final TimestampExtractor timestampExtractor) {
        return new SafeConsumed<>(keySerde, valueSerde, timestampExtractor, null, null);
    }

    public static <K, V> SafeConsumed<K, V> with(final Serde<K> keySerde, final Serde<V> valueSerde, final TimestampExtractor timestampExtractor, final AutoOffsetReset resetPolicy) {
        return new SafeConsumed<>(keySerde, valueSerde, timestampExtractor, resetPolicy, null);
    }

    public static <K, V> SafeConsumed<K, V> with(final Serde<K> keySerde, final Serde<V> valueSerde, final TimestampExtractor timestampExtractor, final AutoOffsetReset resetPolicy, final String processorName) {
        return new SafeConsumed<>(keySerde, valueSerde, timestampExtractor, resetPolicy, processorName);
    }
}
