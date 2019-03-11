package mr.api;

import com.austinv11.servicer.Service;
import org.reactivestreams.Publisher;
import reactor.util.annotation.NonNull;

/**
 * This represents a source of arbitrary data payloads. Implementations are able to programmatically negotiate how
 * payloads are being stored based on provided types.
 */
@Service
public interface DataSource extends Comparable<DataSource> {

    /**
     * Prepares the DataSource to receive a payload type at runtime.
     *
     * @param payloadType The payload type to prepare for.
     * @return A publisher which completes once the source is prepared for the given payload type.
     *
     * @see mr.annotations.Reactant
     */
    Publisher<?> prime(Class<?> payloadType);

    /**
     * Pushes a stream of payload instances into the data source.
     *
     * @param payloadType The type of payload being pushed.
     * @param payload The stream of payloads.
     * @return A publisher which completes once the payload stream is exhausted and the source has completed any storage
     * operations.
     */
    <T> Publisher<?> push(Class<T> payloadType, Publisher<? extends T> payload);

    /**
     * Prepares a new query on the data source for the provided payload type, implicitly upon its primary index.
     *
     * @param payloadType The payload type to query for.
     * @return A {@link mr.api.DataStream} for preparing the query.
     *
     * @see mr.api.DataStream
     * @see mr.annotations.PrimaryIndex
     */
    <K, T> DataStream<K, T> query(Class<T> payloadType);

    /**
     * Prepares a new query on the data source for the provided payload type, it will extract the provided secondary
     * index to use as a key for the first step of the query.
     *
     * @param payloadType The payload type to query for.
     * @param secondaryKeyName The secondary index to query on.
     * @return A {@link mr.api.DataStream} for preparing the query.
     *
     * @see mr.api.DataStream
     * @see mr.annotations.SecondaryIndex
     */
    default <K, T> DataStream<K, T> queryOn(Class<T> payloadType, String secondaryKeyName) {
        return query(payloadType).switchKeys(secondaryKeyName);
    }

    /**
     * The priority of the data source. This is useful for determing the most useful implementation given an environment
     * with multiple implementations.
     *
     * @return The priority, lower values have higher priority.
     */
    default int priority() {
        return 0;
    }

    @Override
    default int compareTo(@NonNull DataSource o) {
        return Integer.compare(this.priority(), o.priority());
    }
}
