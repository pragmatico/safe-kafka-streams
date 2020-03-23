package co.pragmati.kstreams.safe;

import co.pragmati.kstreams.safe.keyvalues.SafeKey;
import co.pragmati.kstreams.safe.keyvalues.SafeKeyValue;
import co.pragmati.kstreams.safe.keyvalues.SafeValue;
import lombok.AllArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.kstream.ValueMapperWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.TopicNameExtractor;

@AllArgsConstructor(staticName = "of")
public class SafeKStreamImpl<K, V> implements SafeKStream<K, V> {

    private final KStream<SafeKey<K>, SafeValue<V>> kstream;

    // TODO: implement SafeKeyValueMapper, ... as a way to get a SafeKStream / SafeValueKStream ...
    // so it's possible to use a generic kstream and convert the kstream to a safeKstream type by applying a `map`
    // with one of the Safe/KeyValueMappers provided by the library

    private <K1, V1> SafeKStream<K1, V1> safe(final KStream<SafeKey<K1>, SafeValue<V1>> kstream) {
        return new SafeKStreamImpl(kstream);
    }

    public KStream<K, V> unsafe() {
        // TODO: is it safe to return nulls if error
        // write log warning if error & throw exception?
        return kstream.map((k, v) -> KeyValue.pair(k.getKey(), v.getValue()));
    }

    public KStream<SafeKey<K>, SafeValue<V>> unwrap() {
        return kstream.map((k, v) -> KeyValue.pair(k, v));
    }

    @Override
    public SafeKStream<K, V> filter(Predicate<? super K, ? super V> predicate) {
        return safe(kstream.filter((k, v) -> SafeKeyValue.pair(k, v).is(predicate)));
    }

    @Override
    public SafeKStream<K, V> filter(Predicate<? super K, ? super V> predicate, Named named) {
        return safe(kstream.filter((k, v) -> SafeKeyValue.pair(k, v).is(predicate), named));
    }

    @Override
    public SafeKStream<K, V> filterNot(Predicate<? super K, ? super V> predicate) {
        return safe(kstream.filterNot((k, v) -> SafeKeyValue.pair(k, v).is(predicate)));
    }

    @Override
    public SafeKStream<K, V> filterNot(Predicate<? super K, ? super V> predicate, Named named) {
        return safe(kstream.filterNot((k, v) -> SafeKeyValue.pair(k, v).is(predicate), named));
    }

    @Override
    public <KR> SafeKStream<KR, V> selectKey(KeyValueMapper<? super K, ? super V, ? extends KR> mapper) {
        return safe(kstream.selectKey((k, v) -> SafeKeyValue.pair(k, v).selectKey(mapper)));
    }

    @Override
    public <KR> SafeKStream<KR, V> selectKey(KeyValueMapper<? super K, ? super V, ? extends KR> mapper, Named named) {
        return safe(kstream.selectKey((k, v) -> SafeKeyValue.pair(k, v).selectKey(mapper), named));
    }

    @Override
    public <KR, VR> SafeKStream<KR, VR> map(KeyValueMapper<? super K, ? super V, ? extends KeyValue<? extends KR, ? extends VR>> mapper) {
        return safe(kstream.map((k, v) -> SafeKeyValue.pair(k, v).map(mapper)));
    }

    @Override
    public <KR, VR> SafeKStream<KR, VR> map(KeyValueMapper<? super K, ? super V, ? extends KeyValue<? extends KR, ? extends VR>> mapper, Named named) {
        return safe(kstream.map((k, v) -> SafeKeyValue.pair(k, v).map(mapper), named));
    }

    @Override
    public <VR> SafeKStream<K, VR> mapValues(ValueMapper<? super V, ? extends VR> mapper) {
        return safe(kstream.mapValues(v -> v.map(mapper)));
    }

    @Override
    public <VR> SafeKStream<K, VR> mapValues(ValueMapper<? super V, ? extends VR> mapper, Named named) {
        return safe(kstream.mapValues(v -> v.map(mapper), named));
    }

    @Override
    public <VR> SafeKStream<K, VR> mapValues(ValueMapperWithKey<? super K, ? super V, ? extends VR> mapper) {
        return safe(kstream.mapValues((k, v) -> SafeKeyValue.pair(k, v).mapValues(mapper)));
    }

    @Override
    public <VR> SafeKStream<K, VR> mapValues(ValueMapperWithKey<? super K, ? super V, ? extends VR> mapper, Named named) {
        return safe(kstream.mapValues((k, v) -> SafeKeyValue.pair(k, v).mapValues(mapper), named));
    }

    @Override
    public <KR, VR> SafeKStream<KR, VR> flatMap(KeyValueMapper<? super K, ? super V, ? extends Iterable<? extends KeyValue<? extends KR, ? extends VR>>> mapper) {
        return safe(kstream.flatMap((k, v) -> SafeKeyValue.pair(k, v).flatMap(mapper)));
    }

    @Override
    public <KR, VR> SafeKStream<KR, VR> flatMap(KeyValueMapper<? super K, ? super V, ? extends Iterable<? extends KeyValue<? extends KR, ? extends VR>>> mapper, Named named) {
        return safe(kstream.flatMap((k, v) -> SafeKeyValue.pair(k, v).flatMap(mapper), named));
    }

    @Override
    public <VR> SafeKStream<K, VR> flatMapValues(ValueMapper<? super V, ? extends Iterable<? extends VR>> mapper) {
        return safe(kstream.flatMapValues(v -> v.flatMapValues(mapper)));
    }

    @Override
    public <VR> SafeKStream<K, VR> flatMapValues(ValueMapper<? super V, ? extends Iterable<? extends VR>> mapper, Named named) {
        return safe(kstream.flatMapValues(v -> v.flatMapValues(mapper), named));
    }

    @Override
    public <VR> SafeKStream<K, VR> flatMapValues(ValueMapperWithKey<? super K, ? super V, ? extends Iterable<? extends VR>> mapper) {
        return safe(kstream.flatMapValues((k, v) -> SafeKeyValue.pair(k, v).flatMapValues(mapper)));
    }

    @Override
    public <VR> SafeKStream<K, VR> flatMapValues(ValueMapperWithKey<? super K, ? super V, ? extends Iterable<? extends VR>> mapper, Named named) {
        return safe(kstream.flatMapValues((k, v) -> SafeKeyValue.pair(k, v).flatMapValues(mapper), named));
    }

    @Override
    public void print(Printed<K, V> printed) {
        unsafe().print(printed);
    }

    @Override
    public void foreach(ForeachAction<? super K, ? super V> action) {
        kstream.foreach((k, v) -> SafeKeyValue.pair(k, v).foreach(action));
    }

    @Override
    public void foreach(ForeachAction<? super K, ? super V> action, Named named) {
        kstream.foreach((k, v) -> SafeKeyValue.pair(k, v).foreach(action), named);
    }

    @Override
    public SafeKStream<K, V> peek(ForeachAction<? super K, ? super V> action) {
        return safe(kstream.peek((k, v) -> SafeKeyValue.pair(k, v).peek(action)));
    }

    @Override
    public SafeKStream<K, V> peek(ForeachAction<? super K, ? super V> action, Named named) {
        return safe(kstream.peek((k, v) -> SafeKeyValue.pair(k, v).peek(action), named));
    }

    @Override
    public SafeKStream<K, V>[] branch(Predicate<? super K, ? super V>... predicates) {
        return new SafeKStream[0];
    }

    @Override
    public SafeKStream<K, V>[] branch(Named named, Predicate<? super K, ? super V>... predicates) {
        return new SafeKStream[0];
    }

    @Override
    public SafeKStream<K, V> merge(SafeKStream<K, V> stream) {
        return safe(kstream.merge(stream.unwrap()));
    }

    @Override
    public SafeKStream<K, V> merge(SafeKStream<K, V> stream, Named named) {
        return safe(kstream.merge(stream.unwrap(), named));
    }

    @Override
    public SafeKStream<K, V> through(String topic) {
        this.unsafe().through(topic);
        return safe(kstream);
    }

    @Override
    public SafeKStream<K, V> through(String topic, Produced<K, V> produced) {
        this.unsafe().through(topic, produced);
        return safe(kstream);
    }

    @Override
    public void to(String topic) {
        this.unsafe().to(topic);
    }

    @Override
    public void to(String topic, Produced<K, V> produced) {
        this.unsafe().to(topic, produced);
    }

    @Override
    public void to(TopicNameExtractor<K, V> topicExtractor) {
        this.unsafe().to(topicExtractor);
    }

    @Override
    public void to(TopicNameExtractor<K, V> topicExtractor, Produced<K, V> produced) {
        this.unsafe().to(topicExtractor, produced);
    }

    @Override
    public <K1, V1> SafeKStream<K1, V1> transform(TransformerSupplier<? super K, ? super V, KeyValue<K1, V1>> transformerSupplier, String... stateStoreNames) {
        return null;
    }

    @Override
    public <K1, V1> SafeKStream<K1, V1> transform(TransformerSupplier<? super K, ? super V, KeyValue<K1, V1>> transformerSupplier, Named named, String... stateStoreNames) {
        return null;
    }

    @Override
    public <K1, V1> SafeKStream<K1, V1> flatTransform(TransformerSupplier<? super K, ? super V, Iterable<KeyValue<K1, V1>>> transformerSupplier, String... stateStoreNames) {
        return null;
    }

    @Override
    public <K1, V1> SafeKStream<K1, V1> flatTransform(TransformerSupplier<? super K, ? super V, Iterable<KeyValue<K1, V1>>> transformerSupplier, Named named, String... stateStoreNames) {
        return null;
    }

    @Override
    public <VR> SafeKStream<K, VR> transformValues(ValueTransformerSupplier<? super V, ? extends VR> valueTransformerSupplier, String... stateStoreNames) {
        return null;
    }

    @Override
    public <VR> SafeKStream<K, VR> transformValues(ValueTransformerSupplier<? super V, ? extends VR> valueTransformerSupplier, Named named, String... stateStoreNames) {
        return null;
    }

    @Override
    public <VR> SafeKStream<K, VR> transformValues(ValueTransformerWithKeySupplier<? super K, ? super V, ? extends VR> valueTransformerSupplier, String... stateStoreNames) {
        return null;
    }

    @Override
    public <VR> SafeKStream<K, VR> transformValues(ValueTransformerWithKeySupplier<? super K, ? super V, ? extends VR> valueTransformerSupplier, Named named, String... stateStoreNames) {
        return null;
    }

    @Override
    public <VR> SafeKStream<K, VR> flatTransformValues(ValueTransformerSupplier<? super V, Iterable<VR>> valueTransformerSupplier, String... stateStoreNames) {
        return null;
    }

    @Override
    public <VR> SafeKStream<K, VR> flatTransformValues(ValueTransformerSupplier<? super V, Iterable<VR>> valueTransformerSupplier, Named named, String... stateStoreNames) {
        return null;
    }

    @Override
    public <VR> SafeKStream<K, VR> flatTransformValues(ValueTransformerWithKeySupplier<? super K, ? super V, Iterable<VR>> valueTransformerSupplier, String... stateStoreNames) {
        return null;
    }

    @Override
    public <VR> SafeKStream<K, VR> flatTransformValues(ValueTransformerWithKeySupplier<? super K, ? super V, Iterable<VR>> valueTransformerSupplier, Named named, String... stateStoreNames) {
        return null;
    }

    @Override
    public void process(ProcessorSupplier<? super K, ? super V> processorSupplier, String... stateStoreNames) {

    }

    @Override
    public void process(ProcessorSupplier<? super K, ? super V> processorSupplier, Named named, String... stateStoreNames) {

    }

    @Override
    public KGroupedStream<K, V> groupByKey() {
        return null;
    }

    @Override
    public KGroupedStream<K, V> groupByKey(Grouped<K, V> grouped) {
        return null;
    }

    @Override
    public <KR> KGroupedStream<KR, V> groupBy(KeyValueMapper<? super K, ? super V, KR> selector) {
        return null;
    }

    @Override
    public <KR> KGroupedStream<KR, V> groupBy(KeyValueMapper<? super K, ? super V, KR> selector, Grouped<KR, V> grouped) {
        return null;
    }

    @Override
    public <VO, VR> SafeKStream<K, VR> join(SafeKStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows) {
        return null;
    }

    @Override
    public <VO, VR> SafeKStream<K, VR> join(SafeKStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows, StreamJoined<K, V, VO> streamJoined) {
        return null;
    }

    @Override
    public <VO, VR> SafeKStream<K, VR> leftJoin(SafeKStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows) {
        return null;
    }

    @Override
    public <VO, VR> SafeKStream<K, VR> leftJoin(SafeKStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows, StreamJoined<K, V, VO> streamJoined) {
        return null;
    }

    @Override
    public <VO, VR> SafeKStream<K, VR> outerJoin(SafeKStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows) {
        return null;
    }

    @Override
    public <VO, VR> SafeKStream<K, VR> outerJoin(SafeKStream<K, VO> otherStream, ValueJoiner<? super V, ? super VO, ? extends VR> joiner, JoinWindows windows, StreamJoined<K, V, VO> streamJoined) {
        return null;
    }

    @Override
    public <VT, VR> SafeKStream<K, VR> join(KTable<K, VT> table, ValueJoiner<? super V, ? super VT, ? extends VR> joiner) {
        return null;
    }

    @Override
    public <VT, VR> SafeKStream<K, VR> join(KTable<K, VT> table, ValueJoiner<? super V, ? super VT, ? extends VR> joiner, Joined<K, V, VT> joined) {
        return null;
    }

    @Override
    public <VT, VR> SafeKStream<K, VR> leftJoin(KTable<K, VT> table, ValueJoiner<? super V, ? super VT, ? extends VR> joiner) {
        return null;
    }

    @Override
    public <VT, VR> SafeKStream<K, VR> leftJoin(KTable<K, VT> table, ValueJoiner<? super V, ? super VT, ? extends VR> joiner, Joined<K, V, VT> joined) {
        return null;
    }

    @Override
    public <GK, GV, RV> SafeKStream<K, RV> join(GlobalKTable<GK, GV> globalKTable, KeyValueMapper<? super K, ? super V, ? extends GK> keyValueMapper, ValueJoiner<? super V, ? super GV, ? extends RV> joiner) {
        return null;
    }

    @Override
    public <GK, GV, RV> SafeKStream<K, RV> join(GlobalKTable<GK, GV> globalKTable, KeyValueMapper<? super K, ? super V, ? extends GK> keyValueMapper, ValueJoiner<? super V, ? super GV, ? extends RV> joiner, Named named) {
        return null;
    }

    @Override
    public <GK, GV, RV> SafeKStream<K, RV> leftJoin(GlobalKTable<GK, GV> globalKTable, KeyValueMapper<? super K, ? super V, ? extends GK> keyValueMapper, ValueJoiner<? super V, ? super GV, ? extends RV> valueJoiner) {
        return null;
    }

    @Override
    public <GK, GV, RV> SafeKStream<K, RV> leftJoin(GlobalKTable<GK, GV> globalKTable, KeyValueMapper<? super K, ? super V, ? extends GK> keyValueMapper, ValueJoiner<? super V, ? super GV, ? extends RV> valueJoiner, Named named) {
        return null;
    }
}
