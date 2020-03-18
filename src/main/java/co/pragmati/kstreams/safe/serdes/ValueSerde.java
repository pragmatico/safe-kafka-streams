package co.pragmati.kstreams.safe.serdes;

import co.pragmati.kstreams.safe.keyvalues.SafeValue;
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
public class ValueSerde<T> implements Serializer<SafeValue<T>>, Deserializer<SafeValue<T>>, Serde<SafeValue<T>> {

    private Serde<T> serde;

    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey) {
        serde.configure(configs, isKey);
    }

    @Override
    public SafeValue<T> deserialize(final String topic, final byte[] data) {
        return SafeValue.of(data).map(d -> serde.deserializer().deserialize(null, data));
    }

    @Override
    public byte[] serialize(final String topic, final SafeValue<T> data) {
        return data.map(v -> serde.serializer().serialize(null, v)).getValue();
    }

    @Override
    public void close() {
        serde.close();
    }

    @Override
    public Serializer<SafeValue<T>> serializer() {
        return this;
    }

    @Override
    public Deserializer<SafeValue<T>> deserializer() {
        return this;
    }
}