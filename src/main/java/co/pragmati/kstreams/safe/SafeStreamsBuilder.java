package co.pragmati.kstreams.safe;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.internals.ConsumedInternal;
import org.apache.kafka.streams.kstream.internals.InternalStreamsBuilder;
import org.apache.kafka.streams.kstream.internals.MaterializedInternal;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.apache.kafka.streams.processor.internals.InternalTopologyBuilder;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

public class SafeStreamsBuilder extends StreamsBuilder {

    public SafeStreamsBuilder() {
        super();
    }

    public synchronized <K, V> SafeKStream<K, V> stream(String topic) {
        return super.stream((Collection) Collections.singleton(topic));
    }

    public synchronized <K, V> SafeKStream<K, V> stream(String topic, Consumed<K, V> consumed) {
        return this.stream((Collection)Collections.singleton(topic), consumed);
    }

    public synchronized <K, V> SafeKStream<K, V> stream(Collection<String> topics) {
        return this.stream(topics, Consumed.with((Serde)null, (Serde)null, (TimestampExtractor)null, (Topology.AutoOffsetReset)null));
    }

    public synchronized <K, V> KStream<K, V> stream(Collection<String> topics, Consumed<K, V> consumed) {
        return super.stream(topics, new ConsumedInternal(consumed));
    }

    public synchronized <K, V> KStream<K, V> stream(Pattern topicPattern) {
        return this.stream(topicPattern, Consumed.with((Serde)null, (Serde)null));
    }

    public synchronized <K, V> KStream<K, V> stream(Pattern topicPattern, Consumed<K, V> consumed) {
        return super.stream(topicPattern, new ConsumedInternal(consumed));
    }

    public synchronized <K, V> KTable<K, V> table(String topic, Consumed<K, V> consumed, Materialized<K, V, KeyValueStore<Bytes, byte[]>> materialized) {
        return super.table(topic, consumedInternal, materializedInternal);
    }

    public synchronized <K, V> KTable<K, V> table(String topic) {
        return this.table(topic, (Consumed)(new ConsumedInternal()));
    }

    public synchronized <K, V> KTable<K, V> table(String topic, Consumed<K, V> consumed) {
        Objects.requireNonNull(topic, "topic can't be null");
        Objects.requireNonNull(consumed, "consumed can't be null");
        ConsumedInternal<K, V> consumedInternal = new ConsumedInternal(consumed);
        MaterializedInternal<K, V, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(Materialized.with(consumedInternal.keySerde(), consumedInternal.valueSerde()), this.internalStreamsBuilder, topic + "-");
        return this.internalStreamsBuilder.table(topic, consumedInternal, materializedInternal);
    }

    public synchronized <K, V> KTable<K, V> table(String topic, Materialized<K, V, KeyValueStore<Bytes, byte[]>> materialized) {
        Objects.requireNonNull(topic, "topic can't be null");
        Objects.requireNonNull(materialized, "materialized can't be null");
        MaterializedInternal<K, V, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(materialized, this.internalStreamsBuilder, topic + "-");
        ConsumedInternal<K, V> consumedInternal = new ConsumedInternal(Consumed.with(materializedInternal.keySerde(), materializedInternal.valueSerde()));
        return this.internalStreamsBuilder.table(topic, consumedInternal, materializedInternal);
    }

    public synchronized <K, V> GlobalKTable<K, V> globalTable(String topic, Consumed<K, V> consumed) {
        Objects.requireNonNull(topic, "topic can't be null");
        Objects.requireNonNull(consumed, "consumed can't be null");
        ConsumedInternal<K, V> consumedInternal = new ConsumedInternal(consumed);
        MaterializedInternal<K, V, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(Materialized.with(consumedInternal.keySerde(), consumedInternal.valueSerde()), this.internalStreamsBuilder, topic + "-");
        return this.internalStreamsBuilder.globalTable(topic, consumedInternal, materializedInternal);
    }

    public synchronized <K, V> GlobalKTable<K, V> globalTable(String topic) {
        return this.globalTable(topic, Consumed.with((Serde)null, (Serde)null));
    }

    public synchronized <K, V> GlobalKTable<K, V> globalTable(String topic, Consumed<K, V> consumed, Materialized<K, V, KeyValueStore<Bytes, byte[]>> materialized) {
        Objects.requireNonNull(topic, "topic can't be null");
        Objects.requireNonNull(consumed, "consumed can't be null");
        Objects.requireNonNull(materialized, "materialized can't be null");
        ConsumedInternal<K, V> consumedInternal = new ConsumedInternal(consumed);
        materialized.withKeySerde(consumedInternal.keySerde()).withValueSerde(consumedInternal.valueSerde());
        MaterializedInternal<K, V, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(materialized, this.internalStreamsBuilder, topic + "-");
        return this.internalStreamsBuilder.globalTable(topic, consumedInternal, materializedInternal);
    }

    public synchronized <K, V> GlobalKTable<K, V> globalTable(String topic, Materialized<K, V, KeyValueStore<Bytes, byte[]>> materialized) {
        Objects.requireNonNull(topic, "topic can't be null");
        Objects.requireNonNull(materialized, "materialized can't be null");
        MaterializedInternal<K, V, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(materialized, this.internalStreamsBuilder, topic + "-");
        return this.internalStreamsBuilder.globalTable(topic, new ConsumedInternal(Consumed.with(materializedInternal.keySerde(), materializedInternal.valueSerde())), materializedInternal);
    }

    public synchronized StreamsBuilder addStateStore(StoreBuilder builder) {
        Objects.requireNonNull(builder, "builder can't be null");
        this.internalStreamsBuilder.addStateStore(builder);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public synchronized StreamsBuilder addGlobalStore(StoreBuilder storeBuilder, String topic, String sourceName, Consumed consumed, String processorName, ProcessorSupplier stateUpdateSupplier) {
        Objects.requireNonNull(storeBuilder, "storeBuilder can't be null");
        Objects.requireNonNull(consumed, "consumed can't be null");
        this.internalStreamsBuilder.addGlobalStore(storeBuilder, sourceName, topic, new ConsumedInternal(consumed), processorName, stateUpdateSupplier);
        return this;
    }

    public synchronized StreamsBuilder addGlobalStore(StoreBuilder storeBuilder, String topic, Consumed consumed, ProcessorSupplier stateUpdateSupplier) {
        Objects.requireNonNull(storeBuilder, "storeBuilder can't be null");
        Objects.requireNonNull(consumed, "consumed can't be null");
        this.internalStreamsBuilder.addGlobalStore(storeBuilder, topic, new ConsumedInternal(consumed), stateUpdateSupplier);
        return this;
    }

    public synchronized Topology build() {
        return this.build((Properties)null);
    }

    public synchronized Topology build(Properties props) {
        this.internalStreamsBuilder.buildAndOptimizeTopology(props);
        return this.topology;
    }
}
