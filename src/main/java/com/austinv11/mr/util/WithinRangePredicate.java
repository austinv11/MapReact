package com.austinv11.mr.util;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * This is a predicate implementation which tests whether a provided object is within range of the starting and
 * ending objects.
 *
 * @param <T> The (comparable) type to use.
 */
public class WithinRangePredicate<T extends Comparable<T>> implements Predicate<T> {

    private final T start;
    private final T end;
    private final boolean exclusive;

    /**
     * Constructs the predicate.
     *
     * @param start The starting object.
     * @param end The ending object.
     * @param exclusive Whether checks are exclusive.
     */
    public WithinRangePredicate(@Nullable T start, @Nullable T end, boolean exclusive) {
        this.start = start;
        this.end = end;
        if (start == null && end == null)
            throw new NullPointerException("Range end points cannot both be null!");
        this.exclusive = exclusive;
    }

    @Override
    public boolean test(T t) {
        if (start != null && end != null) { //Inside range
            if (exclusive)
                return start.compareTo(t) < 0 && end.compareTo(t) > 0;
            else
                return start.compareTo(t) <= 0 && end.compareTo(t) >= 0;
        } else if (start != null) { //Greater than
            if (exclusive)
                return start.compareTo(t) < 0;
            else
                return start.compareTo(t) <= 0;
        } else if (end != null) { //Less than
            if (exclusive)
                return end.compareTo(t) > 0;
            else
                return end.compareTo(t) >= 0;
        } else {
            throw new NullPointerException();
        }
    }
}
