package mr.annotations;

import java.lang.annotation.*;

/**
 * A marker annotation for a payload which is to be inserted into a {@link mr.api.DataSource}. This allows for
 * implementations to utilize annotation scanning at compile-time to optimize potential data structures and queries.
 * <p>
 * <b>Note:</b> It is expected every payload contain at least one {@link mr.annotations.PrimaryIndex} field and may or
 * may not contain one or more {@link mr.annotations.SecondaryIndex} fields.
 *
 * @see mr.api.DataSource
 * @see mr.annotations.PrimaryIndex
 * @see mr.annotations.SecondaryIndex
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Payload {
}
