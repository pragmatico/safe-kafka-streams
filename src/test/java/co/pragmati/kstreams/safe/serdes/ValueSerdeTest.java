package co.pragmati.kstreams.safe.serdes;

import co.pragmati.kstreams.safe.containers.ValueContainer;
import co.pragmati.kstreams.safe.stubs.SampleEvent;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ValueSerdeTest {

    private final ValueSerde<SampleEvent> eventValueSerde = ValueSerde.of(JacksonJsonSerde.forClass(SampleEvent.class));

    @Test
    public void testDeserializationSuccessful() {
        final byte[] json = SampleEvent.jsonSample();
        final ValueContainer<SampleEvent> v = eventValueSerde.deserialize(null, json);
        assertThat(v).isNotNull();
        assertThat(v.getValue().getId()).isEqualTo(SampleEvent.EXPECTED_ID);
        assertThat(v.getValue().getName()).isEqualTo(SampleEvent.EXPECTED_NAME);
    }

    @Test
    public void testDeserializationError() {
        final byte[] json = "{invalid!!!}".getBytes(StandardCharsets.UTF_8);
        final ValueContainer<SampleEvent> v = eventValueSerde.deserialize(null, json);
        assertThat(v.failed()).isTrue();
    }

    @Test
    public void testSerializeOutputSuccessfully() throws IOException {
        final ValueContainer<SampleEvent> input = ValueContainer.of("".getBytes()).map(a -> SampleEvent.sample());
        final byte[] output = eventValueSerde.serialize(null, input);
        assertThat(output).isNotNull();
        final SampleEvent ev = MAPPER.readValue(output, SampleEvent.class);
        assertThat(ev).isEqualTo(SampleEvent.sample());
    }

    @Test
    public void testClose() {
        assertThat(catchThrowable(() -> ValueSerde.of(JacksonJsonSerde.forClass(SampleEvent.class)).close())).doesNotThrowAnyException();
    }

    @Test
    public void testConfigure() {
        assertThat(catchThrowable(() -> ValueSerde.of(JacksonJsonSerde.forClass(SampleEvent.class)).configure(Map.of(), true))).doesNotThrowAnyException();
    }

    @Test
    public void testGetSerializer() {
        assertThat(eventValueSerde.serializer()).isEqualTo(eventValueSerde);
    }
}