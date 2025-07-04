package io.micronaut.jimmer;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MicronautJimmerTest {

    private static EmbeddedServer server;

    @BeforeAll
    static void setup() {
        server = ApplicationContext.run(EmbeddedServer.class);
    }

    @AfterAll
    static void teardown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void testItWorks() {
        Assertions.assertTrue(server.isRunning());
    }
}
