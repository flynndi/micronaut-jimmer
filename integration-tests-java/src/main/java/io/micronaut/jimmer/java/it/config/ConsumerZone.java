package io.micronaut.jimmer.java.it.config;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.ZoneId;
import java.util.function.Consumer;
import org.babyfish.jimmer.sql.JSqlClient;

@Factory
public class ConsumerZone {

    @Singleton
    @Named(Constant.DEFAULT)
    public Consumer<JSqlClient.Builder> jSqlClientZoneIdConfigurerForDefaultDb() {
        return builder -> builder.setZoneId(ZoneId.of("Asia/Shanghai"));
    }

    @Singleton
    @Named(Constant.DATASOURCE2)
    public Consumer<JSqlClient.Builder> jSqlClientZoneIdConfigurerForDb2() {
        return builder -> builder.setZoneId(ZoneId.of("Asia/Chongqing"));
    }
}
