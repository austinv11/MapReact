package com.austinv11.mr.impl.jdk;

import com.austinv11.mr.api.operators.WithinRangeOperator;
import com.austinv11.mr.util.WithinRangePredicate;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class JdkWithinRangeOperator<K extends Comparable<K>, T>
        extends WithinRangeOperator<JdkWithinRangeOperator<K, T>, K, T, JdkDataStream<K, T>> {

    private final Predicate<K> predicate;

    public JdkWithinRangeOperator(@Nullable K lower, @Nullable K upper, boolean exclusive, boolean flip) {
        this(new WithinRangePredicate<>(lower, upper, exclusive), flip);
    }

    private JdkWithinRangeOperator(Predicate<K> predicate, boolean flip) {
        this.predicate = flip ? predicate.negate() : predicate;
    }

    @Override
    public JdkWithinRangeOperator<K, T> not() {
        return new JdkWithinRangeOperator<>(predicate, true);
    }

    @Override
    public JdkDataStream<K, T> map(JdkDataStream<K, T> dataStream) {
        return new JdkDataStream<>(dataStream.dsp, dataStream.rootType,
                dataStream.remaining.filter(t -> predicate.test(t.getT1())));
    }
}
