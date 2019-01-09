package com.austinv11.mr.impl.jdk;

import com.austinv11.mr.api.DataStream;
import com.austinv11.mr.api.DataStreamProvider;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

public class JdkDataStream<K, T> implements DataStream<K, T, JdkDataStream<K, T>> {

    protected final JdkDataStreamProvider dsp;
    protected final Class<?> rootType;
    protected final Flux<Tuple2<K, T>> remaining;

    public JdkDataStream(JdkDataStreamProvider dsp, Class<?> rootType, Flux<Tuple2<K, T>> remaining) {
        this.dsp = dsp;
        this.rootType = rootType;
        this.remaining = remaining;
    }

    public JdkDataSource dataSource() {
        return dsp.ds;
    }

    @Override
    public DataStreamProvider provider() {
        return dsp;
    }
}
