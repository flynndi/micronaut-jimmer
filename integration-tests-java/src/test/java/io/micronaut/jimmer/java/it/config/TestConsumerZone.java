package io.micronaut.jimmer.java.it.config;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.ZoneId;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class TestConsumerZone {

    @Inject JSqlClient jSqlClient;

    @Inject
    @Named(Constant.DATASOURCE2)
    JSqlClient jSqlClientDB2;

    @Test
    void testConsumer() {
        JSqlClientImplementor defaultJSqlClientImplementor = (JSqlClientImplementor) jSqlClient;
        ZoneId zoneId = defaultJSqlClientImplementor.getZoneId();

        JSqlClientImplementor jSqlClientImplementorDB2 = (JSqlClientImplementor) jSqlClientDB2;
        ZoneId zoneIdDB2 = jSqlClientImplementorDB2.getZoneId();

        Assertions.assertEquals(zoneId, ZoneId.of("Asia/Shanghai"));
        Assertions.assertEquals(zoneIdDB2, ZoneId.of("Asia/Chongqing"));
    }
}
