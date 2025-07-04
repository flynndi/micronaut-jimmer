package io.micronaut.jimmer.cfg;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.jimmer.SqlClients;
import jakarta.inject.Singleton;
import javax.sql.DataSource;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.cache.CacheAbandonedCallback;
import org.babyfish.jimmer.sql.kt.KSqlClient;

@Factory
@Internal
@Singleton
public class SqlClientConfig {

    @Singleton
    @Requires(property = "micronaut.jimmer.language", value = "java")
    @EachBean(DataSource.class)
    public JSqlClient javaSqlClient(ApplicationContext ctx, @Parameter String dataSourceName) {
        return SqlClients.java(
                ctx,
                ctx.getBean(DataSource.class, Qualifiers.byName(dataSourceName)),
                dataSourceName);
    }

    @Singleton
    @Requires(property = "micronaut.jimmer.language", value = "kotlin")
    @EachBean(DataSource.class)
    public KSqlClient kotlinSqlClient(ApplicationContext ctx, @Parameter String dataSourceName) {
        return SqlClients.kotlin(
                ctx,
                ctx.getBean(DataSource.class, Qualifiers.byName(dataSourceName)),
                dataSourceName);
    }

    @Singleton
    @Requires(missingBeans = CacheAbandonedCallback.class)
    public CacheAbandonedCallback cacheAbandonedCallback() {
        return CacheAbandonedCallback.log();
    }
}
