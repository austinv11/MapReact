package com.austinv11.mr.api;

import org.reactivestreams.Publisher;

public interface DataSource extends Comparable<DataSource> {

    DataStreamProvider provider();

    <T> Publisher<Void> makeAware(String primaryKey, Class<T> valueType);

    <T> Publisher<Void> push(Class<T> type, Publisher<T> values);

    default <T, DS extends DataStream<Void, T, DS>> DS stream(Class<T> type) {
        return provider().getStreamOf(type);
    }

    default <K, T, DS extends DataStream<K, T, DS>> DS stream(String keyName, Class<T> valueType) {
        return provider().getStreamOf(keyName, valueType);
    }

    default int priority() {
        return 0;
    }

    @Override
    default int compareTo(DataSource o) {
        return Integer.compare(this.priority(), o.priority());
    }
}
