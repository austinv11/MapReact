package com.austinv11.mr.impl.jdk;

import com.austinv11.mr.api.operators.DeleteOperator;
import org.reactivestreams.Publisher;

public class JdkDeleteOperator extends DeleteOperator<JdkDataStream<Object, Object>> {

    @Override
    public Publisher<? extends Void> react(JdkDataStream<Object, Object> dataStream) {
        return dataStream.remaining.doOnNext(t -> dataStream.dataSource().remove(dataStream.rootType, t)).then();
    }
}
