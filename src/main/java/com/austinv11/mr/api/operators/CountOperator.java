package com.austinv11.mr.api.operators;

import com.austinv11.mr.api.DataStream;
import com.austinv11.mr.api.ReactionOperator;

public abstract class CountOperator<DS extends DataStream<?, ?, DS>> implements ReactionOperator<Object, Object, Long, DS> {
}
