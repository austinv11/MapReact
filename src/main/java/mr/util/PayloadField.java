package mr.util;

import mr.annotations.PrimaryIndex;
import mr.annotations.SecondaryIndex;

import java.lang.reflect.Field;
import java.util.function.Function;

public final class PayloadField {

    private final String name;
    private final Class<?> type;
    private final boolean isForeign;
    private final ReferenceInfo foreignReference; //Nullable
    private final IndexType indexType;
    private final Function<Object, Object> getter;

    public static PayloadField fromField(Field field) {
        PrimaryIndex primaryIndex = field.getAnnotation(PrimaryIndex.class);
        SecondaryIndex secondaryIndex = field.getAnnotation(SecondaryIndex.class);

        if (primaryIndex != null && secondaryIndex != null)
            throw new AssertionError("A field cannot have both @PrimaryIndex and @SecondaryIndex!");

        String name = field.getName();
        Class<?> type = field.getType();
        IndexType indexType;
        ReferenceInfo foreign;

        if (primaryIndex != null) {
            indexType = IndexType.PRIMARY;
            if (primaryIndex.refersTo().equals(Void.class)) {
                foreign = null;
            } else {
                foreign = new ReferenceInfo(primaryIndex.refersTo(),
                        primaryIndex.foreignName().isEmpty() ? name : primaryIndex.foreignName());
            }
        } else if (secondaryIndex != null) {
            indexType = IndexType.SECONDARY;
            if (secondaryIndex.refersTo().equals(Void.class)) {
                foreign = null;
            } else {
                foreign = new ReferenceInfo(secondaryIndex.refersTo(),
                        secondaryIndex.foreignName().isEmpty() ? name : secondaryIndex.foreignName());
            }
        } else {
            indexType = IndexType.NONE;
            foreign = null;
        }

        return new PayloadField(name, type, foreign != null, foreign, indexType,
                FieldAccessors.getAccessor()
                        .makeGetter((Class<Object>)field.getDeclaringClass(), (Class<Object>) type, name));
    }

    public PayloadField(String name, Class<?> type, boolean isForeign, ReferenceInfo foreignReference,
                        IndexType indexType, Function<Object, Object> getter) {
        this.name = name;
        this.type = type;
        this.isForeign = isForeign;
        this.foreignReference = foreignReference;
        this.indexType = indexType;
        this.getter = getter;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isForeign() {
        return isForeign;
    }

    public ReferenceInfo getForeignReference() {
        return foreignReference;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public Object getValue(Object instance) {
        return getter.apply(instance);
    }

    public static final class ReferenceInfo {

        private final Class<?> referenceClass;
        private final String foreignFieldName;

        public ReferenceInfo(Class<?> referenceClass, String foreignFieldName) {
            this.referenceClass = referenceClass;
            this.foreignFieldName = foreignFieldName;
        }

        public Class<?> getReferenceClass() {
            return referenceClass;
        }

        public String getForeignFieldName() {
            return foreignFieldName;
        }
    }

    public enum IndexType {
        PRIMARY, SECONDARY, NONE
    }
}
