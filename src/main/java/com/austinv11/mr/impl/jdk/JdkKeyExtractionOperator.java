package com.austinv11.mr.impl.jdk;

import com.austinv11.mr.api.operators.KeyExtractionOperator;
import reactor.util.function.Tuples;

public class JdkKeyExtractionOperator<OK, NK, T> extends KeyExtractionOperator<OK, NK, T, JdkDataStream<OK, T>, JdkDataStream<NK, T>> {

    public JdkKeyExtractionOperator(String name) {
        super(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JdkDataStream<NK, T> map(JdkDataStream<OK, T> dataStream) {
        return new JdkDataStream<>(dataStream.dsp, dataStream.rootType, dataStream.remaining
                .map(t -> Tuples.of((NK) dataStream.dataSource().getFieldValue(t.getT2(), getName()), t.getT2())));
    }
}
