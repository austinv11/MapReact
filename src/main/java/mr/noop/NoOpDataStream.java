package mr.noop;

import mr.api.DataStream;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class NoOpDataStream<K, T> implements DataStream<K, T> {

    @Override
    public DataStream<K, T> requireKey(K key) {
        return this;
    }

    @Override
    public DataStream<K, T> requireUniqueKeys() {
        return this;
    }

    @Override
    public DataStream<K, T> requireKeyInRange(K lower, K upper, boolean endInclusive) {
        return this;
    }

    @Override
    public DataStream<K, T> requireKeyLessThan(K key, boolean andEqualTo) {
        return this;
    }

    @Override
    public DataStream<K, T> requireKeyGreaterThan(K key, boolean andEqualTo) {
        return this;
    }

    @Override
    public <K2> DataStream<K2, T> switchKeys(String keyName) {
        return new NoOpDataStream<>();
    }

    @Override
    public Publisher<Long> count() {
        return Mono.just(0L);
    }

    @Override
    public Publisher<?> delete() {
        return Mono.empty();
    }

    @Override
    public Publisher<? extends T> pull() {
        return Mono.empty();
    }

    @Override
    public Publisher<? extends K> pullKeys() {
        return Mono.empty();
    }
}
