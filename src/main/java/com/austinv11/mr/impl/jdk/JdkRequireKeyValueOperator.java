package com.austinv11.mr.impl.jdk;

import com.austinv11.mr.api.operators.RequireKeyValueOperator;

import java.util.Objects;

public class JdkRequireKeyValueOperator<K, T>
        extends RequireKeyValueOperator<JdkRequireKeyValueOperator<K, T>, K, T, JdkDataStream<K, T>> {

    private final boolean matchEquals;

    public JdkRequireKeyValueOperator(K value) {
        this(value, true);
    }

    public JdkRequireKeyValueOperator(K value, boolean matchEquals) {
        super(value);
        this.matchEquals = matchEquals;
    }

    @Override
    public JdkRequireKeyValueOperator<K, T> not() {
        return new JdkRequireKeyValueOperator<>(getValue(), !matchEquals);
    }

    @Override
    public JdkDataStream<K, T> map(JdkDataStream<K, T> dataStream) {
        return new JdkDataStream<>(dataStream.dsp, dataStream.rootType,
                dataStream.remaining.filter(t -> matchEquals == Objects.equals(t.getT1(), getValue())));
    }
}
