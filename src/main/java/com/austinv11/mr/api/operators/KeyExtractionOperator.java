package com.austinv11.mr.api.operators;

import com.austinv11.mr.api.DataStream;
import com.austinv11.mr.api.MappingOperator;

public abstract class KeyExtractionOperator<OK, NK, T,
        ODS extends DataStream<OK, T, ODS>, NDS extends DataStream<NK, T, NDS>>
        implements MappingOperator<OK, T, ODS, NK, T, NDS> {

    private final String name;

    protected KeyExtractionOperator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
