package com.austinv11.mr.api;

public interface MappingOperator<K, T, DS extends DataStream<? extends K, ? extends T, DS>,
        NK, NT, NDS extends DataStream<? extends NK, ? extends NT, NDS>> {

    NDS map(DS dataStream);
}
