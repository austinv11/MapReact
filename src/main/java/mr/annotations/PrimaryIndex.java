package mr.annotations;

import java.lang.annotation.*;

/**
 * A marker annotation for a field in a payload which is "primary". This means that the given field is what is primarily
 * used to query for payloads. This allows for potentially hyper-optimized queries against a given field.
 * <p>
 * <b>Note:</b> Only one primary index is allowed for any given payload and they need not be unique!
 *
 * @see mr.annotations.Payload
 * @see mr.annotations.SecondaryIndex
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryIndex {

    /**
     * An optional property. If the given index depends on another payload's index, a relationship can be formed. This
     * would result in operations like {@link mr.api.DataStream#delete()} cascading its effects to other groups of
     * payloads in order to propagate a change.
     *
     * @return The foreign payload class this refers to.
     *
     * @see #foreignName()
     */
    Class<?> refersTo() default Void.class;

    /**
     * If {@link #refersTo()} contains a payload class, this property maps the current index to the name as it appears
     * in the foreign payload.
     * <p>
     * <b>Note:</b> If this is not provided, field's name is linked against the field of the same name in the foreign
     * payload.
     *
     * @return The foreign index's name.
     *
     * @see #refersTo()
     */
    String foreignName() default "";
}
