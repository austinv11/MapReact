package mr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class FieldAccessors {

    private final static FieldAccessor ACCESSOR;

    static {
        List<FieldAccessor> accessors = new ArrayList<>();
        ServiceLoader.load(FieldAccessor.class).iterator().forEachRemaining(accessors::add);
        ACCESSOR = accessors.stream().sorted().findFirst().get();
    }

    public static FieldAccessor getAccessor() {
        return ACCESSOR;
    }
}
