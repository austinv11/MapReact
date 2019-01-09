package com.austinv11.mr.impl.jdk;

import com.austinv11.mr.api.operators.KeyAsValueOperator;
import reactor.util.function.Tuples;

public class JdkKeyAsValueOperator<K> extends KeyAsValueOperator<K, JdkDataStream<K, Object>, JdkDataStream<K, K>> {

    @Override
    public JdkDataStream<K, K> map(JdkDataStream<K, Object> dataStream) {
        return new JdkDataStream<>(dataStream.dsp, dataStream.rootType, dataStream.remaining.map(t -> Tuples.of(t.getT1(), t.getT1())));
    }
}
