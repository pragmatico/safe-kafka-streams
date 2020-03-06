package co.pragmati.kstreams.safe.containers;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * Predicates that check if the outcome of the transformation is a success or failure.
 * To help with readability, methods take the pair <K, V> to allow the use of method references
 * in the KStreams filter operation.
 */
@NoArgsConstructor(access = PRIVATE)
public class ValuePredicates {

    @SuppressWarnings("squid:S1172")
    public static <K, V> boolean isError(K key, ValueContainer<V> value) {
        return value.failed();
    }

    public static <K, V> boolean isSuccess(K key, ValueContainer<V> value) {
        return !isError(key, value);
    }
}