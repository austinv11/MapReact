package com.austinv11.mr.impl.jdk;

import com.austinv11.mr.api.operators.CountOperator;
import org.reactivestreams.Publisher;

public class JdkCountOperator extends CountOperator<JdkDataStream<Object, Object>> {

    @Override
    public Publisher<? extends Long> react(JdkDataStream dataStream) {
        return dataStream.remaining.count();
    }
}
