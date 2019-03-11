package mr;

import discord4j.store.api.Store;
import discord4j.store.jdk.JdkStore;
import discord4j.store.jdk.JdkStoreService;
import mr.annotations.Reactant;
import mr.annotations.PrimaryIndex;
import mr.annotations.SecondaryIndex;
import mr.api.DataSource;
import mr.jdk.JdkDataSource;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.Serializable;
import java.util.ServiceLoader;

import static org.junit.Assert.*;

public class Tests {

    @Test
    public void test() {
        DataSource ds = new JdkDataSource();

        Mono.from(ds.prime(TestPayload.class))
                .then(Mono.from(ds.push(TestPayload.class, Flux.fromArray(
                        new TestPayload[] {
                                new TestPayload(0, 4),
                                new TestPayload(1, 5),
                                new TestPayload(2, 6),
                                new TestPayload(3, 7)
                        }))))
                .block();

        //noinspection ConstantConditions
        assertEquals(4L, (long) Mono.from(ds.query(TestPayload.class).count()).block());

        TestPayload payload = Mono.from(ds.query(TestPayload.class).requireKey(0L).pull()).block();
        assertEquals(0L, payload.id);
        assertEquals(4L, payload.composedId);

        TestPayload payload2 = Mono.from(
                ds.queryOn(TestPayload.class, "composedId").requireKey(7L).pull()).block();
        assertEquals(3L, payload2.id);
        assertEquals(7L, payload2.composedId);

        TestPayload payload3 = Mono.from(
                ds.query(TestPayload.class).requireKey(1L).switchKeys("composedId").requireKey(5L).pull()).block();
        assertEquals(1L, payload3.id);
        assertEquals(5L, payload3.composedId);

        long payloads = Mono.from(ds.query(TestPayload.class).requireKeyInRange(1L, 3L).count()).block();
        assertEquals(2L, payloads);
    }

    @Test
    public void testStores() {
        JdkStoreService service = new JdkStoreService();
        Store<Long, TestPayload> store = service.provideGenericStore(Long.class, TestPayload.class);

        store.save(Flux.fromArray(
                new Tuple2[] {
                        Tuples.of(0L, new TestPayload(0, 4)),
                        Tuples.of(1L, new TestPayload(1, 5)),
                        Tuples.of(2L, new TestPayload(2, 6)),
                        Tuples.of(3L, new TestPayload(3, 7))
                })).block();

        //noinspection ConstantConditions
        assertEquals(4L, (long) store.count().block());

        TestPayload payload = store.find(0L).block();
        assertEquals(0L, payload.id);
        assertEquals(4L, payload.composedId);

        TestPayload payload2 = store.values().filter(it -> it.composedId == 7).blockFirst();
        assertEquals(3L, payload2.id);
        assertEquals(7L, payload2.composedId);

        TestPayload payload3 = store.find(1L).filter(it -> it.composedId == 5).block();
        assertEquals(1L, payload3.id);
        assertEquals(5L, payload3.composedId);

        long payloads = store.findInRange(1L, 3L).count().block();
        assertEquals(2L, payloads);
    }

    @Reactant
    public static class TestPayload implements Serializable {

        @PrimaryIndex
        private final long id;

        @SecondaryIndex
        private final long composedId;

        public TestPayload(long id, long composedId) {
            this.id = id;
            this.composedId = composedId;
        }
    }
}
