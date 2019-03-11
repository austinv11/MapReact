package mr.util;

import com.austinv11.servicer.Service;

import java.util.function.Function;

@Service
public interface FieldAccessor extends Comparable<FieldAccessor> {

    Class<?> fieldType(Class<?> type, String name);

    <O, F> Function<O, F> makeGetter(Class<O> holderClazz, Class<F> fieldType, String fieldName);

    int priority();

    @Override
    default int compareTo(FieldAccessor o) {
        return Integer.compare(this.priority(), o.priority());
    }
}
