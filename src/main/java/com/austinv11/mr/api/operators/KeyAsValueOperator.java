package com.austinv11.mr.api.operators;

import com.austinv11.mr.api.DataStream;
import com.austinv11.mr.api.MappingOperator;

public abstract class KeyAsValueOperator<K, DS extends DataStream<K, ?, DS>, NDS extends DataStream<K, K, NDS>>
        implements MappingOperator<K, Object, DS, K, K, NDS> {
}
