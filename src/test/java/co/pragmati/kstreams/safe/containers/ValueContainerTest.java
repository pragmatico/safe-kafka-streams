package co.pragmati.kstreams.safe.containers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ValueContainerTest {

    private static final byte[] JSON = "{}".getBytes();

    @Test
    public void testCreateValueContainer() {
        final ValueContainer<byte[]> v = ValueContainer.of(JSON);
        assertThat(v).isNotNull();
        assertThat(v.failed()).isFalse();
        assertThat(v.getValue()).isEqualTo(JSON);
        assertThat(v.getSource()).isEqualTo(JSON);
    }

    @Test
    public void testMapValue() {
        final ValueContainer<byte[]> v = ValueContainer.of(JSON);
        assertThat(v.map(a -> a.toString().concat("-modified")).getValue()).isEqualTo(JSON.toString().concat("-modified"));
    }

    @Test
    public void testMappingError() {
        final ValueContainer<byte[]> v = ValueContainer.of(JSON);
        final ValueContainer<byte[]> v2 = v.map(a -> {
            throw new RuntimeException("wowww");
        });
        assertThat(v2.failed()).isTrue();
        assertThat(v2.getSource()).isEqualTo(JSON);
        assertThat(v2.getException().get()).hasMessageContaining("wowww");
    }

    @Test
    public void testIs() {
        final String expectedText = "hello";
        final ValueContainer<String> v = ValueContainer.of(JSON).map(notUsed -> expectedText);
        assertThat(v.is(a -> a == a)).isTrue();
        assertThat(v.is(a -> a == "invalid")).isFalse();
    }

    @Test
    public void testIsWhenPredicateFails() {
        final String expectedText = "hello";
        final ValueContainer<String> v = ValueContainer.of(JSON).map(notUsed -> expectedText);
        assertThat(v.is(a -> {
            throw new RuntimeException("yuhuu");
        })).isFalse();
    }

    @Test
    public void testIsWhenFailedValue() {
        final ValueContainer<byte[]> v = ValueContainer.of(JSON).map(a -> {
            throw new RuntimeException("wowww");
        });
        assertThat(v.is(a -> a == a)).isFalse();
    }
}
