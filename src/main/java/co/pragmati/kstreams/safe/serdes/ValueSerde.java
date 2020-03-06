package co.pragmati.kstreams.safe.serdes;

import co.pragmati.kstreams.safe.containers.ValueContainer;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Parameterised Serde that provides safe serialization/deserialization from/to ValueContainer
 * parameterised types
 *
 * @param <T>
 */
@AllArgsConstructor(staticName = "of")
public class ValueSerde<T> implements Serializer<ValueContainer<T>>, Deserializer<ValueContainer<T>>, Serde<ValueContainer<T>> {

    private Serde<T> serde;

    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey) {
        serde.configure(configs, isKey);
    }

    @Override
    public ValueContainer<T> deserialize(final String topic, final byte[] data) {
        return ValueContainer.of(data).map(d -> serde.deserializer().deserialize(null, data));
    }

    @Override
    public byte[] serialize(final String topic, final ValueContainer<T> data) {
        return data.map(v -> serde.serializer().serialize(null, v)).getValue();
    }

    @Override
    public void close() {
        serde.close();
    }

    @Override
    public Serializer<ValueContainer<T>> serializer() {
        return this;
    }

    @Override
    public Deserializer<ValueContainer<T>> deserializer() {
        return this;
    }
}