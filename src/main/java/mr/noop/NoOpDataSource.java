package mr.noop;

import mr.api.DataSource;
import mr.api.DataStream;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class NoOpDataSource implements DataSource {

    @Override
    public Publisher<?> prime(Class<?> payloadType) {
        return Mono.empty();
    }

    @Override
    public <T> Publisher<?> push(Class<T> payloadType, Publisher<? extends T> payload) {
        return Mono.empty();
    }

    @Override
    public <K, T> DataStream<K, T> query(Class<T> payloadType) {
        return new NoOpDataStream<>();
    }

    @Override
    public <K, T> DataStream<K, T> queryOn(Class<T> payloadType, String secondaryKeyName) {
        return new NoOpDataStream<>();
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }
}
