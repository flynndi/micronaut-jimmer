package io.micronaut.jimmer.kotlin.it.config

import io.micronaut.context.annotation.Factory
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.babyfish.jimmer.sql.JSqlClient
import java.time.ZoneId
import java.util.function.Consumer

@Factory
class ConsumerZone {
    @Singleton
    @Named(Constant.DEFAULT)
    fun jSqlClientZoneIdConfigurerForDefaultDb(): Consumer<JSqlClient.Builder> =
        Consumer { builder: JSqlClient.Builder ->
            builder.setZoneId(ZoneId.of("Asia/Shanghai"))
        }

    @Singleton
    @Named(Constant.DATASOURCE2)
    fun jSqlClientZoneIdConfigurerForDb2(): Consumer<JSqlClient.Builder> =
        Consumer { builder: JSqlClient.Builder ->
            builder.setZoneId(ZoneId.of("Asia/Chongqing"))
        }
}
