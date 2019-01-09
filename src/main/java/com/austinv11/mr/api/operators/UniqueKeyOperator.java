package com.austinv11.mr.api.operators;

import com.austinv11.mr.api.DataStream;

public abstract class UniqueKeyOperator<SELF extends UniqueKeyOperator<SELF, K, T, DS>,
        K, T, DS extends DataStream<? extends K, ? extends T, DS>> implements FilterOperator<SELF, K, T, DS> {
}
