package mr.jdk;

import com.austinv11.servicer.WireService;
import mr.api.DataSource;
import mr.api.DataStream;
import mr.util.PayloadDefinition;
import mr.util.PayloadField;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@WireService(DataSource.class)
public class JdkDataSource implements DataSource {

    private final Function<Class<?>, Map<Object, Object>> mapGenerator;
    private final Map<Class<?>, Map<Object, Object>> metaMap;
    private final Map<Class<?>, PayloadDefinition> definitionLookup;

    public JdkDataSource() {
        this(clazz -> new HashMap<>());
    }

    public JdkDataSource(Function<Class<?>, Map<Object, Object>> mapGenerator) {
        this.mapGenerator = mapGenerator;
        this.metaMap = new ConcurrentHashMap<>();
        this.definitionLookup = new ConcurrentHashMap<>();
    }

    @Override
    public Publisher<?> prime(Class<?> payloadType) {
        return Mono.fromCallable(() -> mapGenerator.apply(payloadType))
                .doOnNext(map -> metaMap.put(payloadType, map))
                .doOnNext(map -> definitionLookup.put(payloadType, PayloadDefinition.generateDefinition(payloadType)));
    }

    @Override
    public <T> Publisher<?> push(Class<T> payloadType, Publisher<? extends T> payload) {
        return Flux.from(payload)
                .map(obj -> Tuples.of(obj, definitionLookup.get(payloadType)))
                .doOnNext(items -> {
                    T obj = items.getT1();
                    PayloadDefinition def = items.getT2();
                    Object key = def.getPrimaryIndex().getValue(obj);
                    metaMap.get(payloadType).put(key, obj);
                })
                .then();
    }

    @Override
    public <K, T> DataStream<K, T> query(Class<T> payloadType) {
        return new JdkDataStream<>((Map<K, T>) metaMap.get(payloadType), payloadType);
    }

    @Override
    public <K, T> DataStream<K, T> queryOn(Class<T> payloadType, String secondaryKeyName) {
        Map<K, T> map = (Map<K, T>) metaMap.get(payloadType);
        return new JdkDataStream<>(map, payloadType, Flux.defer(() -> Flux.fromIterable(map.keySet())).map(k -> {
            PayloadField field = definitionLookup.get(payloadType).getFields().get(secondaryKeyName);
            T obj = map.get(k);
            return Tuples.of(k, (K) field.getValue(obj), obj);
        }));
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE - 1;
    }
}
