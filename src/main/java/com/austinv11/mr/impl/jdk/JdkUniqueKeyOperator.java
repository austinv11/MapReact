package com.austinv11.mr.impl.jdk;

import com.austinv11.mr.api.operators.UniqueKeyOperator;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class JdkUniqueKeyOperator<K, T> extends UniqueKeyOperator<JdkUniqueKeyOperator<K, T>, K, T, JdkDataStream<K, T>> {

    private final boolean requireUnique;

    public JdkUniqueKeyOperator() {
        this(true);
    }

    public JdkUniqueKeyOperator(boolean requireUnique) {
        this.requireUnique = requireUnique;
    }

    @Override
    public JdkUniqueKeyOperator<K, T> not() {
        return new JdkUniqueKeyOperator<>(!requireUnique);
    }

    @Override
    public JdkDataStream<K, T> map(JdkDataStream<K, T> dataStream) {
        if (requireUnique)
            return new JdkDataStream<>(dataStream.dsp, dataStream.rootType, dataStream.remaining.distinct(Tuple2::getT1));
        else
            return new JdkDataStream<>(dataStream.dsp, dataStream.rootType,
                    dataStream.remaining.groupBy(Tuple2::getT1)
                            .flatMap(Flux::collectList)
                            .filter(l -> l.size() > 1) //Non-unique should have been sharing groups
                            .flatMapIterable(l -> l));
    }
}
