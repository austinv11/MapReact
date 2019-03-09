package mr.jdk;

import mr.api.DataSource;
import mr.api.DataStream;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class JdkDataSource implements DataSource {

    private final Function<Class<?>, Map<?, ?>> mapGenerator;
    private final Map<Class<?>, Map<?, ?>> metaMap;

    public JdkDataSource() {
        this(clazz -> new HashMap<>());
    }

    public JdkDataSource(Function<Class<?>, Map<?, ?>> mapGenerator) {
        this.mapGenerator = mapGenerator;
        this.metaMap = (Map<Class<?>, Map<?, ?>>) mapGenerator.apply(Map.class);
    }

    @Override
    public Publisher<?> prime(Class<?> payloadType) {
        return Mono.fromCallable(() -> mapGenerator.apply(payloadType)).doOnNext(map -> metaMap.put(payloadType, map));
    }

    @Override
    public <T> Publisher<?> push(Class<T> payloadType, Publisher<? extends T> payload) {
        return Flux.from(payload).doOnNext(item -> metaMap.get(payloadType).put());
    }

    @Override
    public <K, T> DataStream<K, T> query(Class<T> payloadType) {
        return null;
    }

    @Override
    public <K, T> DataStream<K, T> queryOn(Class<T> payloadType, String secondaryKeyName) {
        return null;
    }

    @Override
    public int priority() {
        return 0;
    }
}
