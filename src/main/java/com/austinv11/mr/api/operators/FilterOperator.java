package com.austinv11.mr.api.operators;

import com.austinv11.mr.api.DataStream;
import com.austinv11.mr.api.MappingOperator;

public interface FilterOperator<SELF extends FilterOperator<?, K, T, DS>,
        K, T, DS extends DataStream<? extends K, ? extends T, DS>>
        extends MappingOperator<K, T, DS, K, T, DS> {

    SELF not();
}
