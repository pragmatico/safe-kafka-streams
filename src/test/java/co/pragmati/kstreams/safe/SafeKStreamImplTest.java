package co.pragmati.kstreams.safe;

import co.pragmati.kstreams.safe.serdes.JacksonJsonMapper;
import co.pragmati.kstreams.safe.serdes.JacksonJsonSerde;
import co.pragmati.kstreams.safe.serdes.SafeValueSerde;
import co.pragmati.kstreams.safe.stubs.SampleEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;

public class SafeKStreamImplTest {

    private final String IN_TOPIC = "in-topic";
    private final String OUT_TOPIC = "out-topic";

    private final ObjectMapper MAPPER = JacksonJsonMapper.withDefaults();
    private final JacksonJsonSerde<SampleEvent> eventJsonSerde = JacksonJsonSerde.forClass(SampleEvent.class);
    private final SafeValueSerde<SampleEvent> eventSafeValueSerde = SafeValueSerde.of(eventJsonSerde);

    private Properties kafkaProperties;

    @BeforeEach
    public void setUp() {
        kafkaProperties = new Properties();
        kafkaProperties.put(APPLICATION_ID_CONFIG, "test-stream-service");
        kafkaProperties.put(BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:1234");
    }

    @AfterEach
    public void tearDown() {
    }

    @Nested
    @DisplayName("Basic KStream with safe deserialization/serialization of the kafka message' - happy path")
    class WhenBasicStream {

        @Test
        public void shouldReturnOutput() {
            // given
            byte[] messageKey = "key".getBytes();
            SampleEvent message = SampleEvent.sample();

            // topology
            SafeStreamsBuilder builder = new SafeStreamsBuilder();
            builder.stream(IN_TOPIC, SafeConsumed.with(Serdes.ByteArray(), eventJsonSerde))
                    .to(OUT_TOPIC, Produced.with(Serdes.ByteArray(), eventJsonSerde));

            Topology topology = builder.build();

            TopologyTestDriver testDriver = new TopologyTestDriver(topology, kafkaProperties);

            // when
            TestInputTopic<byte[], SampleEvent> inputTopic = testDriver.createInputTopic(IN_TOPIC, Serdes.ByteArray().serializer(), eventJsonSerde.serializer());
            inputTopic.pipeInput(messageKey, message);

            // then
            TestOutputTopic<byte[], SampleEvent> outputTopic = testDriver.createOutputTopic(OUT_TOPIC, Serdes.ByteArray().deserializer(), eventJsonSerde.deserializer());
            Assertions.assertThat(outputTopic.readKeyValue()).isEqualTo(KeyValue.pair(messageKey, message));
            testDriver.close();
        }

    }

}
