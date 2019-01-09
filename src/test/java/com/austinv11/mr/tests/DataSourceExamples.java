package com.austinv11.mr.tests;

import com.austinv11.mr.api.DataSource;
import com.austinv11.mr.impl.jdk.JdkDataSource;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;

public class DataSourceExamples {

    @Test
    public void examples() {
        DataSource ds = new JdkDataSource();

        Mono.from(ds.makeAware("msg", ExampleDAO.class)).block();

        assertEquals(0, (long) Mono.from(ds.stream(ExampleDAO.class).count()).block());

        Mono.from(ds.push(ExampleDAO.class, Flux.just(new ExampleDAO("Hello", 1), new ExampleDAO("World", 2)))).block();

        assertEquals(2, (long) Mono.from(ds.stream(ExampleDAO.class).count()).block());

        assertEquals(1, Mono.from(ds.stream(ExampleDAO.class).mapWith("i").requireKeyValue(1).keys()).block());
    }

    public static class ExampleDAO {

        private final String msg;
        private final int i;

        public ExampleDAO(String msg, int i) {this.msg = msg;
            this.i = i;
        }
    }

    @Test
    public void discordExample() {
        DataSource ds = new JdkDataSource();

        Mono.from(ds.makeAware("id", ExampleEntity.class)).block();

        Mono.from(ds.push(ExampleEntity.class, Flux.just(new ExampleEntity("Hello", 1, 1),
                new ExampleEntity("World", 2, 1), new ExampleEntity("idk", 3, 2)))).block();

        assertEquals(3L, (long) Mono.from(ds.stream(ExampleEntity.class).count()).block());
        assertEquals(2L, (long) Mono.from(ds.stream("guildId", ExampleEntity.class).requireKeyValue(1L).count()).block());

        assertEquals(2L, (long) Mono.from(ds.stream("id", ExampleEntity.class).withinRange(0L, 3L, true).count()).block());
    }

    public static class ExampleEntity {

        private final String msg;
        private final long id;
        private final long guildId;

        public ExampleEntity(String msg, long id, long guildId) {
            this.msg = msg;
            this.id = id;
            this.guildId = guildId;
        }
    }
}
