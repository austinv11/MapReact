package com.austinv11.mr.api;

import com.austinv11.mr.api.operators.*;

public interface DataStreamProvider {

    //Operations
    <DS extends DataStream<?, ?, DS>> CountOperator<DS> count();

    <K, DS extends DataStream<K, ?, DS>> DeleteOperator<DS> delete();

    <T, DS extends DataStream<?, T, DS>> GetOperator<T, DS> get();

    <K, DS extends DataStream<K, ?, DS>, NDS extends DataStream<K, K, NDS>> KeyAsValueOperator<K, DS, NDS> keysAsValues();

    <OK, NK, T, DS extends DataStream<OK, T, DS>, NDS extends DataStream<NK, T, NDS>>
        KeyExtractionOperator<OK, NK, T, DS, NDS> extractKeys(String keyName);

    <K, T, DS extends DataStream<K, T, DS>, RV extends RequireKeyValueOperator<RV, K, T, DS>> RV requireKeyValue(K key);

    @SuppressWarnings("unchecked")
    default <K, T, DS extends DataStream<K, T, DS>, RV extends RequireKeyValueOperator<RV, K, T, DS>> RV requireNotKeyValue(K key) {
        return (RV) requireKeyValue(key).not();
    }

    <K, T, DS extends DataStream<K, T, DS>, RV extends UniqueKeyOperator<RV, K, T, DS>> RV requireUniqueKeys();

    <K extends Comparable<K>, T, DS extends DataStream<K, T, DS>, RV extends WithinRangeOperator<RV, K, T, DS>>
        RV withinRange(K lowerBound, K upperBound, boolean exclusive);

    <K extends Comparable<K>, T, DS extends DataStream<K, T, DS>, RV extends WithinRangeOperator<RV, K, T, DS>>
        RV lessThan(K upperBound, boolean exclusive);

    <K extends Comparable<K>, T, DS extends DataStream<K, T, DS>, RV extends WithinRangeOperator<RV, K, T, DS>>
        RV greaterThan(K lowerBound, boolean exclusive);

    //DataStream retrievals
    <T, DS extends DataStream<Void, T, DS>> DS getStreamOf(Class<T> type);

    default <K, T, DS extends DataStream<K, T, DS>, TEMP extends DataStream<Void, T, TEMP>> DS getStreamOf(String keyName, Class<T> valueType) {
        KeyExtractionOperator<Void, K, T, TEMP, DS> keo = extractKeys(keyName);
        TEMP t = getStreamOf(valueType);
        return t.apply(keo);
    }
}
