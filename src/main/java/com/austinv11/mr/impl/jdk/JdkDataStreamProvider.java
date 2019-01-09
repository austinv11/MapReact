package com.austinv11.mr.impl.jdk;

import com.austinv11.mr.api.DataStream;
import com.austinv11.mr.api.DataStreamProvider;
import com.austinv11.mr.api.operators.*;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@SuppressWarnings("unchecked")
public class JdkDataStreamProvider implements DataStreamProvider {

    private static final Object DUMMY = new Object();

    protected final JdkDataSource ds;

    public JdkDataStreamProvider(JdkDataSource ds) {
        this.ds = ds;
    }

    @Override
    public <DS extends DataStream<?, ?, DS>> CountOperator<DS> count() {
        return (CountOperator<DS>) new JdkCountOperator();
    }

    @Override
    public <K, DS extends DataStream<K, ?, DS>> DeleteOperator<DS> delete() {
        return (DeleteOperator<DS>) new JdkDeleteOperator();
    }

    @Override
    public <T, DS extends DataStream<?, T, DS>> GetOperator<T, DS> get() {
        return (GetOperator<T, DS>) new JdkGetOperator<T>();
    }

    @Override
    public <K, DS extends DataStream<K, ?, DS>, NDS extends DataStream<K, K, NDS>> KeyAsValueOperator<K, DS
            , NDS> keysAsValues() {
        return (KeyAsValueOperator<K, DS, NDS>) new JdkKeyAsValueOperator<K>();
    }

    @Override
    public <OK, NK, T, DS extends DataStream<OK, T, DS>, NDS extends DataStream<NK, T, NDS>> KeyExtractionOperator<OK
            , NK, T, DS, NDS> extractKeys(String keyName) {
        return (KeyExtractionOperator<OK, NK, T, DS, NDS>) new JdkKeyExtractionOperator<OK, NK, T>(keyName);
    }

    @Override
    public <K, T, DS extends DataStream<K, T, DS>, RV extends RequireKeyValueOperator<RV, K, T, DS>> RV requireKeyValue(K key) {
        return (RV) new JdkRequireKeyValueOperator<K, T>(key);
    }

    @Override
    public <K, T, DS extends DataStream<K, T, DS>, RV extends UniqueKeyOperator<RV, K, T, DS>> RV requireUniqueKeys() {
        return (RV) new JdkUniqueKeyOperator<K, T>();
    }

    @Override
    public <K extends Comparable<K>, T, DS extends DataStream<K, T, DS>,
            RV extends WithinRangeOperator<RV, K, T, DS>> RV withinRange(K lowerBound, K upperBound,
                                                                         boolean exclusive) {
        return (RV) new JdkWithinRangeOperator<K, T>(lowerBound, upperBound, exclusive, false);
    }

    @Override
    public <K extends Comparable<K>, T, DS extends DataStream<K, T, DS>, RV extends WithinRangeOperator<RV, K, T, DS>> RV lessThan(K upperBound, boolean exclusive) {
        return (RV) new JdkWithinRangeOperator<K, T>(null, upperBound, exclusive, false);
    }

    @Override
    public <K extends Comparable<K>, T, DS extends DataStream<K, T, DS>, RV extends WithinRangeOperator<RV, K, T, DS>> RV greaterThan(K lowerBound, boolean exclusive) {
        return (RV) new JdkWithinRangeOperator<K, T>(lowerBound, null, exclusive, false);
    }

    @Override
    public <T, DS extends DataStream<Void, T, DS>> DS getStreamOf(Class<T> type) {
        return (DS) new JdkDataStream<Void, T>(this, type, Flux.defer(() -> {
            return Flux.fromIterable(ds.data.get(type).entrySet())
                    .map(e -> (Tuple2) Tuples.of(DUMMY, e.getValue()));
        }));
    }

    @Override
    public <K, T, DS extends DataStream<K, T, DS>, TEMP extends DataStream<Void, T, TEMP>> DS getStreamOf(String keyName, Class<T> valueType) {
        return (DS) new JdkDataStream<>(this, valueType, Flux.defer(() -> {
            return Flux.fromIterable(ds.data.get(valueType).entrySet())
                    .map(e -> (Tuple2<K, T>) Tuples.of(
                            keyName.equals(ds.type2primary.get(valueType)) ? e.getKey() : ds.getFieldValue(e.getValue(), keyName), 
                            e.getValue()));
        }));
    }
}
