package co.pragmati.kstreams.safe.serdes;

import co.pragmati.kstreams.safe.keyvalues.SafeKey;
import co.pragmati.kstreams.safe.keyvalues.SafeValue;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Parameterised Serde that provides safe serialization/deserialization from/to SafeKey
 * parameterised types
 *
 * @param <T>
 */
@AllArgsConstructor(staticName = "of")
public class SafeKeySerde<T> implements Serializer<SafeKey<T>>, Deserializer<SafeKey<T>>, Serde<SafeKey<T>> {

    private Serde<T> serde;

    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey) {
        serde.configure(configs, isKey);
    }

    @Override
    public SafeKey<T> deserialize(final String topic, final byte[] data) {
        return SafeKey.of(data).map(d -> serde.deserializer().deserialize(null, data));
    }

    @Override
    public byte[] serialize(final String topic, final SafeKey<T> data) {
        return data.map(v -> serde.serializer().serialize(null, v)).getKey();
    }

    @Override
    public void close() {
        serde.close();
    }

    @Override
    public Serializer<SafeKey<T>> serializer() {
        return this;
    }

    @Override
    public Deserializer<SafeKey<T>> deserializer() {
        return this;
    }
}