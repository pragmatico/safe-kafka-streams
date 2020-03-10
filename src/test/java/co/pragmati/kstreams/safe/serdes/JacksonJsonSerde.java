package co.pragmati.kstreams.safe.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

@Builder
public class JacksonJsonSerde<T> implements Serializer<T>, Deserializer<T>, Serde<T> {

  private final ObjectMapper mapper;
  private final Class<T> type;

  private JacksonJsonSerde(final ObjectMapper mapper, final Class<T> type) {
    this.mapper = mapper;
    this.type = type;
  }

  public static <T> JacksonJsonSerde<T> forClass(Class<T> type) {
    return new JacksonJsonSerde(JacksonJsonMapper.withDefaults(), type);
  }

  public static <T> JacksonJsonSerde<T> forClass(final Class<T> type, final ObjectMapper overrideMapper) {
    return new JacksonJsonSerde<>(overrideMapper, type);
  }

  @Override
  public void configure(final Map<String, ?> configs, final boolean isKey) {
    // noop
  }

  public T deserialize(final byte[] data) {
    if (data == null) {
      throw new SerializationException("Error deserializing JSON message: message is null");
    }

    try {
      return mapper.readValue(data, type);
    } catch (final IOException e) {
      throw new SerializationException(e);
    }
  }

  @Override
  public T deserialize(final String topic, final byte[] data) {
    return deserialize(data);
  }

  @Override
  public byte[] serialize(final String topic, final T data) {
    if (data == null) {
      throw new SerializationException("Error serializing JSON message: message is null");
    }

    try {
      return mapper.writeValueAsBytes(data);
    } catch (final Exception e) {
      throw new SerializationException("Error serializing JSON message", e);
    }
  }

  @Override
  public void close() {
    // noop
  }

  @Override
  public Serializer<T> serializer() {
    return this;
  }

  @Override
  public Deserializer<T> deserializer() {
    return this;
  }
}