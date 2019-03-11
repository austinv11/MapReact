package mr.util;

import mr.annotations.Reactant;

import java.lang.reflect.Field;
import java.util.*;

public final class PayloadDefinition {

    private final Class<?> payloadClass;
    private final Map<String, PayloadField> fields;
    private final PayloadField primaryIndex;
    private final PayloadField[] secondaryIndices;

    //TODO: Annotation Processing alternative
    public static PayloadDefinition generateDefinition(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Reactant.class))
            throw new AssertionError("Reactant Definition classes must be annotated with @Reactant!");

        Field[] fields = clazz.getDeclaredFields();
        Map<String, PayloadField> fieldMap = new LinkedHashMap<>();
        PayloadField primaryIndex = null;
        List<PayloadField> secondaryIndices = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);

            PayloadField pField = PayloadField.fromField(field);

            switch (pField.getIndexType()) {
                case NONE:
                    fieldMap.put(pField.getName(), pField);
                    break;
                case PRIMARY:
                    fieldMap.put(pField.getName(), pField);
                    if (primaryIndex != null)
                        throw new AssertionError("Reactant definitions can only have one primary index!");
                    primaryIndex = pField;
                    break;
                case SECONDARY:
                    fieldMap.put(pField.getName(), pField);
                    secondaryIndices.add(pField);
                    break;
            }
        }

        if (primaryIndex == null)
            throw new AssertionError("Reactant definitions must have a primary index!");

        return new PayloadDefinition(clazz, Collections.unmodifiableMap(fieldMap), primaryIndex, secondaryIndices.toArray(new PayloadField[0]));
    }

    public PayloadDefinition(Class<?> payloadClass, Map<String, PayloadField> fields,
                             PayloadField primaryIndex, PayloadField[] secondaryIndices) {
        this.payloadClass = payloadClass;
        this.fields = fields;
        this.primaryIndex = primaryIndex;
        this.secondaryIndices = secondaryIndices;
    }

    public Class<?> getPayloadClass() {
        return payloadClass;
    }

    public Map<String, PayloadField> getFields() {
        return fields;
    }

    public PayloadField getPrimaryIndex() {
        return primaryIndex;
    }

    public PayloadField[] getSecondaryIndices() {
        return secondaryIndices;
    }
}
