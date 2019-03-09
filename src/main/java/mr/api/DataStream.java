package mr.api;

import org.reactivestreams.Publisher;

/**
 * This represents a potential stream of payloads built on cumulative mapping and reduction steps.
 */
public interface DataStream<K, T> {

    // Reduction ops

    /**
     * Adds a reduction step to require that all keys be equal to the provided value.
     *
     * @param key The key required.
     * @return A new {@link mr.api.DataStream} instance with this step appended to the tail of the step queue.
     */
    DataStream<K, T> requireKey(K key);

    /**
     * Adds a reduction step to require that all keys be unique.
     *
     * @return A new {@link mr.api.DataStream} instance with this step appended to the tail of the step queue.
     */
    DataStream<K, T> requireUniqueKeys();

    /**
     * Adds a reduction step to require that all keys be within the provided range.
     * <p>
     * <b>Note:</b> This requires the key type to be ordinal/comparable.
     *
     * @param lower The lower end of the range for keys (inclusive).
     * @param upper The upper end of the range for keys.
     * @param endInclusive Whether the upper end of the range should be inclusive (true) or exclusive (false).
     * @return A new {@link mr.api.DataStream} instance with this step appended to the tail of the step queue.
     */
    DataStream<K, T> requireKeyInRange(K lower, K upper, boolean endInclusive);

    /**
     * Adds a reduction step to require that all keys be within the provided range.
     * <p>
     * <b>Note:</b> This requires the key type to be ordinal/comparable.
     *
     * @param lower The lower end of the range for keys (inclusive).
     * @param upper The upper end of the range for keys (exclusive).
     * @return A new {@link mr.api.DataStream} instance with this step appended to the tail of the step queue.
     */
    default DataStream<K, T> requireKeyInRange(K lower, K upper) {
        return requireKeyInRange(lower, upper, false);
    }

    /**
     * Adds a reduction step to require that all keys be less than the provided key.
     * <p>
     * <b>Note:</b> This requires the key type to be ordinal/comparable.
     *
     * @param key The upper end of the range for keys.
     * @param andEqualTo Whether the upper end of the range should be inclusive (true) or exclusive (false).
     * @return A new {@link mr.api.DataStream} instance with this step appended to the tail of the step queue.
     */
    DataStream<K, T> requireKeyLessThan(K key, boolean andEqualTo);

    /**
     * Adds a reduction step to require that all keys be less than the provided key.
     * <p>
     * <b>Note:</b> This requires the key type to be ordinal/comparable.
     *
     * @param key The upper end of the range for keys (exclusive).
     * @return A new {@link mr.api.DataStream} instance with this step appended to the tail of the step queue.
     */
    default DataStream<K, T> requireKeyLessThan(K key) {
        return requireKeyLessThan(key, false);
    }

    /**
     * Adds a reduction step to require that all keys be greater than the provided key.
     * <p>
     * <b>Note:</b> This requires the key type to be ordinal/comparable.
     *
     * @param key The lower end of the range for keys.
     * @param andEqualTo Whether the lower end of the range should be inclusive (true) or exclusive (false).
     * @return A new {@link mr.api.DataStream} instance with this step appended to the tail of the step queue.
     */
    DataStream<K, T> requireKeyGreaterThan(K key, boolean andEqualTo);

    /**
     * Adds a reduction step to require that all keys be greater than the provided key.
     * <p>
     * <b>Note:</b> This requires the key type to be ordinal/comparable.
     *
     * @param key The lower end of the range for keys (exclusive).
     * @return A new {@link mr.api.DataStream} instance with this step appended to the tail of the step queue.
     */
    default DataStream<K, T> requireKeyGreaterThan(K key) {
        return requireKeyGreaterThan(key, false);
    }

    // Mapping ops

    /**
     * Maps the keys to become the values of a secondary index.
     *
     * @param keyName The new index field to use as the key for proceeding operations.
     * @return A new {@link mr.api.DataStream} instance with this step appended to the tail of the step queue.
     *
     * @see mr.annotations.SecondaryIndex
     */
    <K2> DataStream<K2, T> switchKeys(String keyName);

    // Terminating mapping ops

    /**
     * Coalesces all the previous steps and transforms it into a number representing the number of elements available
     * given the current queue of steps.
     *
     * @return A publisher which, when subscribed to, completes with the current number of elements satisfying the
     * given criteria.
     */
    Publisher<Long> count();

    /**
     * Coalesces all the previous steps and attempts to delete all the elements available given the current queue of
     * steps.
     * <p>
     * <b>Note:</b> This <i>will</i> delete all child payloads which are linked to this payload!
     *
     * @return A publisher which, when subscribed to, completes after all elements satisfying the given criteria are
     * deleted from the backing data source.
     */
    Publisher<?> delete();

    /**
     * Coalesces all the previous steps and attempts to pull all the elements available given the current queue of
     * steps.
     *
     * @return A publisher which, when subscribed to, completes after emitting all elements which satisfy the given
     * criteria.
     */
    Publisher<? extends T> pull();

    /**
     * Coalesces all the previous steps and attempts to pull all the keys available given the current queue of
     * steps.
     *
     * @return A publisher which, when subscribed to, completes after emitting all keys which satisfy the given
     * criteria.
     */
    Publisher<? extends K> pullKeys();
}
