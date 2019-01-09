package com.austinv11.mr.api.operators;

import com.austinv11.mr.api.DataStream;

public abstract class RequireKeyValueOperator<SELF extends RequireKeyValueOperator<SELF, K, T, DS>,
        K, T, DS extends DataStream<? extends K, ? extends T, DS>> implements FilterOperator<SELF, K, T, DS> {

    private final K value;

    public RequireKeyValueOperator(K value) {
        this.value = value;
    }

    public K getValue() {
        return value;
    }
}
