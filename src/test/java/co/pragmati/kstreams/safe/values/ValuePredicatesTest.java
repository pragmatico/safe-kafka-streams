package co.pragmati.kstreams.safe.values;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValuePredicatesTest {

    private static final String key = "test";
    private static final byte[] JSON = "{}".getBytes();

    @Test
    public void testIsSuccess() {
        final boolean isSuccess = ValuePredicates.isSuccess(key, ValueContainer.of(JSON));
        final boolean isError = ValuePredicates.isError(key, ValueContainer.of(JSON));
        assertThat(isSuccess).isTrue();
        assertThat(isError).isFalse();
    }

    @Test
    public void testIsError() {
        final ValueContainer<byte[]> value = ValueContainer.of(JSON).map(a -> {
            throw new RuntimeException("wowww");
        });
        final boolean isSuccess = ValuePredicates.isSuccess(key, value);
        final boolean isError = ValuePredicates.isError(key, value);
        assertThat(isSuccess).isFalse();
        assertThat(isError).isTrue();
    }

}