package com.austinv11.mr.api.operators;

import com.austinv11.mr.api.DataStream;

public abstract class WithinRangeOperator<SELF extends WithinRangeOperator<SELF, K, T, DS>,
        K extends Comparable<K>, T, DS extends DataStream<K, T, DS>> implements FilterOperator<SELF, K, T, DS> {
}
