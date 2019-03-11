package mr.util;

import com.austinv11.servicer.WireService;

import java.util.function.Function;

@WireService(FieldAccessor.class)
public class DefaultFieldAccessor implements FieldAccessor {

    @Override
    public Class<?> fieldType(Class<?> type, String name) {
        return JdkFieldGetter.fieldType(type, name);
    }

    @Override
    public <O, F> Function<O, F> makeGetter(Class<O> holderClazz, Class<F> fieldType, String fieldName) {
        return JdkFieldGetter.makeGetter(holderClazz, fieldName, fieldType);
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }
}
