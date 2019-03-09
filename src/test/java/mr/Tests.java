package mr;

import mr.annotations.Payload;
import mr.annotations.PrimaryIndex;
import mr.annotations.SecondaryIndex;
import mr.api.DataSource;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.ServiceLoader;

import static org.junit.Assert.*;

public class Tests {

    @Test
    public void test() {
        DataSource ds = ServiceLoader.load(DataSource.class).findFirst().get();

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
        assertEquals(4L, (long) Mono.from(ds.query(DataSource.class).count()).block());

        TestPayload payload = Mono.from(ds.query(TestPayload.class).requireKey(0L).pull()).block();
        assertEquals(0L, payload.id);
        assertEquals(4L, payload.composedId);

        TestPayload payload2 = Mono.from(ds.queryOn(TestPayload.class, "composedId").requireKey(7L).pull()).block();
        assertEquals(3L, payload2.id);
        assertEquals(7L, payload2.composedId);

        TestPayload payload3 = Mono.from(ds.query(TestPayload.class).requireKey(1L).switchKeys("composedId").requireKey(5L).pull()).block();
        assertEquals(1L, payload3.id);
        assertEquals(5L, payload3.composedId);

        long payloads = Mono.from(ds.query(TestPayload.class).requireKeyInRange(1L, 3L).count()).block();
        assertEquals(2L, payloads);
    }

    @Payload
    public static class TestPayload {

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
