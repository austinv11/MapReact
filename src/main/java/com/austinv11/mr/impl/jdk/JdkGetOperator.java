package com.austinv11.mr.impl.jdk;

import com.austinv11.mr.api.operators.GetOperator;
import org.reactivestreams.Publisher;
import reactor.util.function.Tuple2;

public class JdkGetOperator<V> extends GetOperator<V, JdkDataStream<Object, V>> {

    @Override
    public Publisher<? extends V> react(JdkDataStream<Object, V> dataStream) {
        return dataStream.remaining.map(Tuple2::getT2);
    }
}
