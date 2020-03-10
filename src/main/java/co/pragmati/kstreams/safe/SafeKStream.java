package co.pragmati.kstreams.safe;

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
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.kstream.ValueMapperWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.TopicNameExtractor;

public interface SafeKStream<K, V> extends KStream<K, V> {

    SafeKStream<K, V> filter(final Predicate<? super K, ? super V> predicate);

    SafeKStream<K, V> filter(final Predicate<? super K, ? super V> predicate, final Named named);

    SafeKStream<K, V> filterNot(final Predicate<? super K, ? super V> predicate);

    SafeKStream<K, V> filterNot(final Predicate<? super K, ? super V> predicate, final Named named);

    <KR> SafeKStream<KR, V> selectKey(final KeyValueMapper<? super K, ? super V, ? extends KR> mapper);

    <KR> SafeKStream<KR, V> selectKey(final KeyValueMapper<? super K, ? super V, ? extends KR> mapper,
                                                                   final Named named);

    <KR, VR> SafeKStream<KR, VR> map(final KeyValueMapper<? super K, ? super V, ? extends KeyValue<? extends KR, ? extends VR>> mapper);

    <KR, VR> SafeKStream<KR, VR> map(final KeyValueMapper<? super K, ? super V, ? extends KeyValue<? extends KR, ? extends VR>> mapper,
                                                                  final Named named);

    <VR> SafeKStream<K, VR> mapValues(final ValueMapper<? super V, ? extends VR> mapper);

    <VR> SafeKStream<K, VR> mapValues(final ValueMapper<? super V, ? extends VR> mapper,
                                                                   final Named named);

    <VR> SafeKStream<K, VR> mapValues(final ValueMapperWithKey<? super K, ? super V, ? extends VR> mapper);

    <VR> SafeKStream<K, VR> mapValues(final ValueMapperWithKey<? super K, ? super V, ? extends VR> mapper,
                                                                   final Named named);

    <KR, VR> SafeKStream<KR, VR> flatMap(final KeyValueMapper<? super K, ? super V, ? extends Iterable<? extends KeyValue<? extends KR, ? extends VR>>> mapper);

    <KR, VR> SafeKStream<KR, VR> flatMap(final KeyValueMapper<? super K, ? super V, ? extends Iterable<? extends KeyValue<? extends KR, ? extends VR>>> mapper,
                                                                      final Named named);

    <VR> SafeKStream<K, VR> flatMapValues(final ValueMapper<? super V, ? extends Iterable<? extends VR>> mapper);

    <VR> SafeKStream<K, VR> flatMapValues(final ValueMapper<? super V, ? extends Iterable<? extends VR>> mapper,
                                                                       final Named named);

    <VR> SafeKStream<K, VR> flatMapValues(final ValueMapperWithKey<? super K, ? super V, ? extends Iterable<? extends VR>> mapper);

    <VR> SafeKStream<K, VR> flatMapValues(final ValueMapperWithKey<? super K, ? super V, ? extends Iterable<? extends VR>> mapper,
                                                                       final Named named);

    void print(final Printed<K, V> printed);

    void foreach(final ForeachAction<? super K, ? super V> action);

    void foreach(final ForeachAction<? super K, ? super V> action, final Named named);

    SafeKStream<K, V> peek(final ForeachAction<? super K, ? super V> action);

    SafeKStream<K, V> peek(final ForeachAction<? super K, ? super V> action, final Named named);

    @SuppressWarnings("unchecked")
    SafeKStream<K, V>[] branch(final Predicate<? super K, ? super V>... predicates);

    @SuppressWarnings("unchecked")
    SafeKStream<K, V>[] branch(final Named named, final Predicate<? super K, ? super V>... predicates);

    SafeKStream<K, V> merge(final SafeKStream<K, V> stream);

    SafeKStream<K, V> merge(final SafeKStream<K, V> stream, final Named named);

    SafeKStream<K, V> through(final String topic);

    SafeKStream<K, V> through(final String topic,
                                                           final Produced<K, V> produced);

    void to(final String topic);

    void to(final String topic,
            final Produced<K, V> produced);

    void to(final TopicNameExtractor<K, V> topicExtractor);

    void to(final TopicNameExtractor<K, V> topicExtractor,
            final Produced<K, V> produced);

    <K1, V1> SafeKStream<K1, V1> transform(final TransformerSupplier<? super K, ? super V, KeyValue<K1, V1>> transformerSupplier,
                                                                        final String... stateStoreNames);

    <K1, V1> SafeKStream<K1, V1> transform(final TransformerSupplier<? super K, ? super V, KeyValue<K1, V1>> transformerSupplier,
                                                                        final Named named,
                                                                        final String... stateStoreNames);

    <K1, V1> SafeKStream<K1, V1> flatTransform(final TransformerSupplier<? super K, ? super V, Iterable<KeyValue<K1, V1>>> transformerSupplier,
                                                                            final String... stateStoreNames);

    <K1, V1> SafeKStream<K1, V1> flatTransform(final TransformerSupplier<? super K, ? super V, Iterable<KeyValue<K1, V1>>> transformerSupplier,
                                                                            final Named named,
                                                                            final String... stateStoreNames);

    <VR> SafeKStream<K, VR> transformValues(final ValueTransformerSupplier<? super V, ? extends VR> valueTransformerSupplier,
                                                                         final String... stateStoreNames);

    <VR> SafeKStream<K, VR> transformValues(final ValueTransformerSupplier<? super V, ? extends VR> valueTransformerSupplier,
                                                                         final Named named,
                                                                         final String... stateStoreNames);

    <VR> SafeKStream<K, VR> transformValues(final ValueTransformerWithKeySupplier<? super K, ? super V, ? extends VR> valueTransformerSupplier,
                                                                         final String... stateStoreNames);

    <VR> SafeKStream<K, VR> transformValues(final ValueTransformerWithKeySupplier<? super K, ? super V, ? extends VR> valueTransformerSupplier,
                                                                         final Named named,
                                                                         final String... stateStoreNames);

    <VR> SafeKStream<K, VR> flatTransformValues(final ValueTransformerSupplier<? super V, Iterable<VR>> valueTransformerSupplier,
                                                                             final String... stateStoreNames);

    <VR> SafeKStream<K, VR> flatTransformValues(final ValueTransformerSupplier<? super V, Iterable<VR>> valueTransformerSupplier,
                                                                             final Named named,
                                                                             final String... stateStoreNames);

    <VR> SafeKStream<K, VR> flatTransformValues(final ValueTransformerWithKeySupplier<? super K, ? super V, Iterable<VR>> valueTransformerSupplier,
                                                                             final String... stateStoreNames);


    <VR> SafeKStream<K, VR> flatTransformValues(final ValueTransformerWithKeySupplier<? super K, ? super V, Iterable<VR>> valueTransformerSupplier,
                                                                             final Named named,
                                                                             final String... stateStoreNames);


    void process(final ProcessorSupplier<? super K, ? super V> processorSupplier,
                 final String... stateStoreNames);


    void process(final ProcessorSupplier<? super K, ? super V> processorSupplier,
                 final Named named,
                 final String... stateStoreNames);


    KGroupedStream<K, V> groupByKey();


    @Deprecated
    KGroupedStream<K, V> groupByKey(final Serialized<K, V> serialized);


    KGroupedStream<K, V> groupByKey(final Grouped<K, V> grouped);


    <KR> KGroupedStream<KR, V> groupBy(final KeyValueMapper<? super K, ? super V, KR> selector);


    @Deprecated
    <KR> KGroupedStream<KR, V> groupBy(final KeyValueMapper<? super K, ? super V, KR> selector,
                                       final Serialized<KR, V> serialized);


    <KR> KGroupedStream<KR, V> groupBy(final KeyValueMapper<? super K, ? super V, KR> selector,
                                       final Grouped<KR, V> grouped);


    <VO, VR> SafeKStream<K, VR> join(final SafeKStream<K, VO> otherStream,
                                                                  final ValueJoiner<? super V, ? super VO, ? extends VR> joiner,
                                                                  final JoinWindows windows);


    @Deprecated
    <VO, VR> SafeKStream<K, VR> join(final SafeKStream<K, VO> otherStream,
                                                                  final ValueJoiner<? super V, ? super VO, ? extends VR> joiner,
                                                                  final JoinWindows windows,
                                                                  final Joined<K, V, VO> joined);


    <VO, VR> SafeKStream<K, VR> join(final SafeKStream<K, VO> otherStream,
                                                                  final ValueJoiner<? super V, ? super VO, ? extends VR> joiner,
                                                                  final JoinWindows windows,
                                                                  final StreamJoined<K, V, VO> streamJoined);


    <VO, VR> SafeKStream<K, VR> leftJoin(final SafeKStream<K, VO> otherStream,
                                                                      final ValueJoiner<? super V, ? super VO, ? extends VR> joiner,
                                                                      final JoinWindows windows);


    @Deprecated
    <VO, VR> SafeKStream<K, VR> leftJoin(final SafeKStream<K, VO> otherStream,
                                                                      final ValueJoiner<? super V, ? super VO, ? extends VR> joiner,
                                                                      final JoinWindows windows,
                                                                      final Joined<K, V, VO> joined);


    <VO, VR> SafeKStream<K, VR> leftJoin(final SafeKStream<K, VO> otherStream,
                                                                      final ValueJoiner<? super V, ? super VO, ? extends VR> joiner,
                                                                      final JoinWindows windows,
                                                                      final StreamJoined<K, V, VO> streamJoined);


    <VO, VR> SafeKStream<K, VR> outerJoin(final SafeKStream<K, VO> otherStream,
                                                                       final ValueJoiner<? super V, ? super VO, ? extends VR> joiner,
                                                                       final JoinWindows windows);


    @Deprecated
    <VO, VR> SafeKStream<K, VR> outerJoin(final SafeKStream<K, VO> otherStream,
                                                                       final ValueJoiner<? super V, ? super VO, ? extends VR> joiner,
                                                                       final JoinWindows windows,
                                                                       final Joined<K, V, VO> joined);


    <VO, VR> SafeKStream<K, VR> outerJoin(final SafeKStream<K, VO> otherStream,
                                                                       final ValueJoiner<? super V, ? super VO, ? extends VR> joiner,
                                                                       final JoinWindows windows,
                                                                       final StreamJoined<K, V, VO> streamJoined);


    <VT, VR> SafeKStream<K, VR> join(final KTable<K, VT> table,
                                                                  final ValueJoiner<? super V, ? super VT, ? extends VR> joiner);


    <VT, VR> SafeKStream<K, VR> join(final KTable<K, VT> table,
                                                                  final ValueJoiner<? super V, ? super VT, ? extends VR> joiner,
                                                                  final Joined<K, V, VT> joined);


    <VT, VR> SafeKStream<K, VR> leftJoin(final KTable<K, VT> table,
                                                                      final ValueJoiner<? super V, ? super VT, ? extends VR> joiner);


    <VT, VR> SafeKStream<K, VR> leftJoin(final KTable<K, VT> table,
                                                                      final ValueJoiner<? super V, ? super VT, ? extends VR> joiner,
                                                                      final Joined<K, V, VT> joined);


    <GK, GV, RV> SafeKStream<K, RV> join(final GlobalKTable<GK, GV> globalKTable,
                                                                      final KeyValueMapper<? super K, ? super V, ? extends GK> keyValueMapper,
                                                                      final ValueJoiner<? super V, ? super GV, ? extends RV> joiner);


    <GK, GV, RV> SafeKStream<K, RV> join(final GlobalKTable<GK, GV> globalKTable,
                                                                      final KeyValueMapper<? super K, ? super V, ? extends GK> keyValueMapper,
                                                                      final ValueJoiner<? super V, ? super GV, ? extends RV> joiner,
                                                                      final Named named);


    <GK, GV, RV> SafeKStream<K, RV> leftJoin(final GlobalKTable<GK, GV> globalKTable,
                                                                          final KeyValueMapper<? super K, ? super V, ? extends GK> keyValueMapper,
                                                                          final ValueJoiner<? super V, ? super GV, ? extends RV> valueJoiner);

    
    <GK, GV, RV> SafeKStream<K, RV> leftJoin(final GlobalKTable<GK, GV> globalKTable,
                                                                          final KeyValueMapper<? super K, ? super V, ? extends GK> keyValueMapper,
                                                                          final ValueJoiner<? super V, ? super GV, ? extends RV> valueJoiner,
                                                                          final Named named);

}
