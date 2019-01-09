package com.austinv11.mr.api;

import org.reactivestreams.Publisher;

public interface ReactionOperator<K, T, V, DS extends DataStream<? extends K, ? extends T, DS>> {

    Publisher<? extends V> react(DS dataStream);
}
