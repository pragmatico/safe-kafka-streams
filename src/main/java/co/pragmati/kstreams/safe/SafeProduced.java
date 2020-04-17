package co.pragmati.kstreams.safe;

import org.apache.kafka.streams.kstream.Produced;

public class SafeProduced<K, V> extends Produced<K, V> {

    protected SafeProduced(Produced<K, V> produced) {
        super(produced);
    }
}
