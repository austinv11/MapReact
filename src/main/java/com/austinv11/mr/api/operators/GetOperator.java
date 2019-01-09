package com.austinv11.mr.api.operators;

import com.austinv11.mr.api.DataStream;
import com.austinv11.mr.api.ReactionOperator;

public abstract class GetOperator<V, DS extends DataStream<?, V, DS>> implements ReactionOperator<Object, V, V, DS> {
}
