package com.austinv11.mr.impl.jdk;

import com.austinv11.mr.api.DataSource;
import com.austinv11.mr.api.DataStreamProvider;
import com.austinv11.mr.util.FieldGetter;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class JdkDataSource implements DataSource {

    protected final Map<Class<?>, Map<Object, Object>> data = new ConcurrentHashMap<>();
    protected final Map<Class<?>, String> type2primary = new ConcurrentHashMap<>();
    protected final Map<Tuple2<Class<?>, String>, Function<Object, Object>> getters = new ConcurrentHashMap<>();
    protected final DataStreamProvider provider = new JdkDataStreamProvider(this);

    @Override
    public DataStreamProvider provider() {
        return provider;
    }

    @SuppressWarnings("unchecked")
    void indexGetter(String name, Class type) {
        synchronized (getters) {
            type2primary.putIfAbsent(type, name);
            getters.computeIfAbsent(Tuples.of(type, name),
                    k -> FieldGetter.makeGetter(type, name, FieldGetter.fieldType(type, name)));
        }
    }

    @Override
    public <T> Publisher<Void> makeAware(String primaryKey, Class<T> valueType) {
        return Mono.defer(() -> Mono.just(new ConcurrentHashMap<>())).doOnNext(m -> {
            data.put(valueType, m);
            indexGetter(primaryKey, valueType);
        }).then();
    }

    public Object getFieldValue(Object o, String field) {
        if (!getters.containsKey(Tuples.of(o.getClass(), field)))
            indexGetter(field, o.getClass());
        return getters.get(Tuples.of(o.getClass(), field)).apply(o);
    }

    @Override
    public <T> Publisher<Void> push(Class<T> type, Publisher<T> values) {
        return Mono.defer(() -> Mono.just(data.get(type)))
                .repeat()
                .zipWith(values)
                .doOnNext(t -> t.getT1().put(getFieldValue(t.getT2(), type2primary.get(type)), t.getT2()))
                .then();
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE - 1;
    }

    public void remove(Class<?> rootType, Tuple2<Object, Object> t) {
        data.get(rootType).remove(t.getT1(), t.getT2());
    }
}
