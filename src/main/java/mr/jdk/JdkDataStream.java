package mr.jdk;

import mr.api.DataStream;
import mr.util.FieldAccessor;
import mr.util.FieldAccessors;
import mr.util.Ranges;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class JdkDataStream<K, T> implements DataStream<K, T> {

    private final Map<?, T> map;
    private final Class<?> reactant;
    private final Flux<Tuple3<Object, K, T>> keys; //First key is the primary key, K may or may not be the same

    public JdkDataStream(Map<?, T> map, Class<?> reactant, Flux<Tuple3<Object, K, T>> keys) {
        this.map = map;
        this.reactant = reactant;
        this.keys = keys;
    }

    public JdkDataStream(Map<K, T> map, Class<?> reactant) {
        this(map, reactant, Flux.defer(() -> Flux.fromIterable(map.keySet()))
                .map(k -> Tuples.of(k, k, map.get(k))));
    }

    @Override
    public DataStream<K, T> requireKey(K key) {
        return new JdkDataStream<>(map, reactant, keys.filter(t -> Objects.equals(t.getT2(), key)));
    }

    @Override
    public DataStream<K, T> requireUniqueKeys() {
        return new JdkDataStream<>(map, reactant, keys.distinct(Tuple3::getT2));
    }

    @Override
    public DataStream<K, T> requireKeyInRange(K lower, K upper, boolean endInclusive) {
        Predicate<K> pred = Ranges.withinRange(lower, upper, endInclusive);
        return new JdkDataStream<>(map, reactant, keys.filter(t3 -> pred.test(t3.getT2())));
    }

    @Override
    public DataStream<K, T> requireKeyLessThan(K key, boolean andEqualTo) {
        Predicate<K> pred = Ranges.lessThan(key, andEqualTo);
        return new JdkDataStream<>(map, reactant, keys.filter(t3 -> pred.test(t3.getT2())));
    }

    @Override
    public DataStream<K, T> requireKeyGreaterThan(K key, boolean andEqualTo) {
        Predicate<K> pred = Ranges.greaterThan(key, andEqualTo);
        return new JdkDataStream<>(map, reactant, keys.filter(t3 -> pred.test(t3.getT2())));
    }

    @Override
    public <K2> DataStream<K2, T> switchKeys(String keyName) {
        return new JdkDataStream<>(map, reactant, keys.map(t3 -> {
            FieldAccessor accessor = FieldAccessors.getAccessor();
            K2 newKey = (((Function<T, K2>) accessor.makeGetter(reactant, accessor.fieldType(reactant, keyName), keyName))
                    .apply(t3.getT3()));
            return Tuples.of(t3.getT1(), newKey, t3.getT3());
        }));
    }

    @Override
    public Publisher<Long> count() {
        return keys.count();
    }

    @Override
    public Publisher<?> delete() {
        return keys.doOnNext(t3 -> map.remove(t3.getT1())).then();
    }

    @Override
    public Publisher<? extends T> pull() {
        return keys.map(Tuple3::getT3);
    }

    @Override
    public Publisher<? extends K> pullKeys() {
        return keys.map(Tuple3::getT2);
    }
}
