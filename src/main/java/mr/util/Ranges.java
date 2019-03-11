package mr.util;

import java.util.function.Predicate;

public final class Ranges {

    public static <T> Predicate<T> withinRange(T start, T end, boolean endInclusive) {
        return new Predicate<T>() {
            Predicate<T> greater = greaterThan(start, true);
            Predicate<T> less = lessThan(end, endInclusive);

            @Override
            public boolean test(T t) {
                return greater.test(t) && less.test(t);
            }
        };
    }

    public static <T> Predicate<T> greaterThan(T point, boolean inclusive) {
        return comparing(point, inclusive, false);
    }

    public static <T> Predicate<T> lessThan(T point, boolean inclusive) {
        return comparing(point, inclusive, true);
    }

    private static <T> Predicate<T> comparing(T point, boolean inclusive, boolean lessMode) {
        return new Predicate<T>() {
            @Override
            public boolean test(T t) {
                if (!(t instanceof Comparable) || !(point instanceof Comparable))
                    return true;  //FIXME: Do we want this?

                int comparison = ((Comparable<T>) point).compareTo(t);

                if (comparison == 0)
                    return inclusive;

                return lessMode ? comparison > 0 : comparison < 0;
            }
        };
    }
}
