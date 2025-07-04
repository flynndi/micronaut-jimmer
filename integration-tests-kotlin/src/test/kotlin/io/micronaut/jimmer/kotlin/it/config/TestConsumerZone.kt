package io.micronaut.jimmer.kotlin.it.config

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Named
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.ZoneId

@MicronautTest
class TestConsumerZone {
    @Inject
    lateinit var kSqlClient: KSqlClient

    @Inject
    @Named(Constant.DATASOURCE2)
    lateinit var kSqlClientDB2: KSqlClient

    @Test
    fun testConsumer() {
        val defaultJSqlClientImplementor = kSqlClient.javaClient
        val zoneId = defaultJSqlClientImplementor.zoneId

        val jSqlClientImplementorDB2 = kSqlClientDB2.javaClient
        val zoneIdDB2 = jSqlClientImplementorDB2.zoneId

        Assertions.assertEquals(zoneId, ZoneId.of("Asia/Shanghai"))
        Assertions.assertEquals(zoneIdDB2, ZoneId.of("Asia/Chongqing"))
    }
}
