package co.pragmati.kstreams.safe.serdes;

import co.pragmati.kstreams.safe.keyvalues.SafeValue;
import co.pragmati.kstreams.safe.stubs.SampleEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class SafeValueSerdeTest {

    private final ObjectMapper MAPPER = JacksonJsonMapper.withDefaults();
    private final SafeValueSerde<SampleEvent> eventSafeValueSerde = SafeValueSerde.of(JacksonJsonSerde.forClass(SampleEvent.class));

    @Test
    public void testDeserializationSuccessful() {
        final byte[] json = SampleEvent.jsonSample();
        final SafeValue<SampleEvent> v = eventSafeValueSerde.deserialize(null, json);
        assertThat(v).isNotNull();
        assertThat(v.getValue().getId()).isEqualTo(SampleEvent.EXPECTED_ID);
        assertThat(v.getValue().getName()).isEqualTo(SampleEvent.EXPECTED_NAME);
    }

    @Test
    public void testDeserializationError() {
        final byte[] json = "{invalid!!!}".getBytes(StandardCharsets.UTF_8);
        final SafeValue<SampleEvent> v = eventSafeValueSerde.deserialize(null, json);
        assertThat(v.failed()).isTrue();
    }

    @Test
    public void testSerializeOutputSuccessfully() throws IOException {
        final SafeValue<SampleEvent> input = SafeValue.of("".getBytes()).map(a -> SampleEvent.sample());
        final byte[] output = eventSafeValueSerde.serialize(null, input);
        assertThat(output).isNotNull();
        final SampleEvent ev = MAPPER.readValue(output, SampleEvent.class);
        assertThat(ev).isEqualTo(SampleEvent.sample());
    }

    @Test
    public void testClose() {
        assertThat(catchThrowable(() -> SafeValueSerde.of(JacksonJsonSerde.forClass(SampleEvent.class)).close())).doesNotThrowAnyException();
    }

    @Test
    public void testConfigure() {
        assertThat(catchThrowable(() -> SafeValueSerde.of(JacksonJsonSerde.forClass(SampleEvent.class)).configure(Map.of(), true))).doesNotThrowAnyException();
    }

    @Test
    public void testGetSerializer() {
        assertThat(eventSafeValueSerde.serializer()).isEqualTo(eventSafeValueSerde);
    }
}