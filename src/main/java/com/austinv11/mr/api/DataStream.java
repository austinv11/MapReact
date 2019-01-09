package com.austinv11.mr.api;

import com.austinv11.mr.api.operators.*;
import org.reactivestreams.Publisher;

@SuppressWarnings("unchecked")
public interface DataStream<K, T, SELF extends DataStream<K, T, SELF>> {

    DataStreamProvider provider();

    default <NK, NT, NS extends DataStream<NK, NT, NS>> NS apply(MappingOperator<K, T, SELF, NK, NT, NS> mapper) {
        return mapper.map((SELF) this);
    }

    default <V> Publisher<? extends V> apply(ReactionOperator<K, T, V, SELF> reactor) {
        return reactor.react((SELF) this);
    }

    default <TEMP extends DataStream<K, K, TEMP>> Publisher<? extends K> keys() {
        KeyAsValueOperator<K, SELF, TEMP> op = provider().keysAsValues();
        return op.map((SELF) this).values();
    }

    default Publisher<? extends T> values() {
        GetOperator<T, SELF> get = provider().get();
        return get.react((SELF) this);
    }

    default Publisher<Long> count() {
        CountOperator<SELF> count = provider().count();
        return (Publisher<Long>) count.react((SELF) this);
    }

    default Publisher<Void> delete() {
        DeleteOperator<SELF> delete = provider().delete();
        return (Publisher<Void>) delete.react((SELF) this);
    }

    default SELF requireKeyValue(K value) {
        RequireKeyValueOperator<?, K, T, SELF> op = provider().requireKeyValue(value);
        return op.map((SELF) this);
    }

    default SELF requireNotKeyValue(K value) {
        RequireKeyValueOperator<?, K, T, SELF> op = provider().requireNotKeyValue(value);
        return op.map((SELF) this);
    }

    default SELF uniqueKeys() {
        UniqueKeyOperator<?, K, T, SELF> op = provider().requireUniqueKeys();
        return op.map((SELF) this);
    }

    default <NK, DS extends DataStream<NK, T, DS>> DS mapWith(String name) {
        KeyExtractionOperator<K, NK, T, SELF, DS> op = provider().extractKeys(name);
        return op.map((SELF) this);
    }

    default SELF withinRange(K lowerBound, K upperBound, boolean exclusive) {
        if (!(lowerBound instanceof Comparable) && !(upperBound instanceof Comparable)) throw new ClassCastException();

        return (SELF) provider().withinRange((Comparable) lowerBound, (Comparable) upperBound, exclusive).map(this);
    }

    default SELF lessThan(K upperBound, boolean exclusive) {
        if (!(upperBound instanceof Comparable)) throw new ClassCastException();

        return (SELF) provider().lessThan((Comparable) upperBound, exclusive).map(this);
    }

    default SELF greaterThan(K lowerBound, boolean exclusive) {
        if (!(lowerBound instanceof Comparable)) throw new ClassCastException();

        return (SELF) provider().greaterThan((Comparable) lowerBound, exclusive).map(this);
    }
}
