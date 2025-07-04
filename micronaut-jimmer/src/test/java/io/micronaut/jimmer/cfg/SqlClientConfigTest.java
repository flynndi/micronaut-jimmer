package io.micronaut.jimmer.cfg;

import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.cache.CacheAbandonedCallback;
import org.babyfish.jimmer.sql.kt.KSqlClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SqlClientConfigTest {

    @Test
    void testJavaSqlClientCreatedWhenLanguageJava() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("micronaut.jimmer.language", "java");
        DataSource mock = Mockito.mock(DataSource.class);
        ApplicationContext ctx = ApplicationContext.run(configMap);
        ctx.registerSingleton(DataSource.class, mock, Qualifiers.byName("default"));
        JSqlClient sqlClient = ctx.getBean(JSqlClient.class);
        Assertions.assertNotNull(sqlClient);
        Assertions.assertThrows(
                io.micronaut.context.exceptions.NoSuchBeanException.class,
                () -> ctx.getBean(KSqlClient.class));
        ctx.close();
    }

    @Test
    void testJavaSqlClientCreatedWhenLanguageJavaWithMultipleDataSource() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("micronaut.jimmer.language", "java");
        DataSource mockDataSourceDefault = Mockito.mock(DataSource.class);
        DataSource mockDataSourceAnother = Mockito.mock(DataSource.class);
        ApplicationContext ctx = ApplicationContext.run(configMap);
        ctx.registerSingleton(
                DataSource.class, mockDataSourceDefault, Qualifiers.byName("default"));
        ctx.registerSingleton(
                DataSource.class, mockDataSourceAnother, Qualifiers.byName("another"));
        Collection<JSqlClient> sqlClients = ctx.getBeansOfType(JSqlClient.class);
        Assertions.assertEquals(2, sqlClients.size());
        Assertions.assertNotNull(ctx.getBean(JSqlClient.class, Qualifiers.byName("default")));
        Assertions.assertNotNull(ctx.getBean(JSqlClient.class, Qualifiers.byName("another")));
        Assertions.assertThrows(
                io.micronaut.context.exceptions.NoSuchBeanException.class,
                () -> ctx.getBean(KSqlClient.class));
        ctx.close();
    }

    @Test
    void testKotlinSqlClientCreatedWhenLanguageKotlin() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("micronaut.jimmer.language", "kotlin");
        DataSource mock = Mockito.mock(DataSource.class);
        ApplicationContext ctx = ApplicationContext.run(configMap);
        ctx.registerSingleton(DataSource.class, mock, Qualifiers.byName("default"));
        KSqlClient sqlClient = ctx.getBean(KSqlClient.class);
        Assertions.assertNotNull(sqlClient);
        Assertions.assertThrows(
                io.micronaut.context.exceptions.NoSuchBeanException.class,
                () -> ctx.getBean(JSqlClient.class));
        ctx.close();
    }

    @Test
    void testKotlinSqlClientCreatedWhenLanguageKotlinWithMultipleDataSource() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("micronaut.jimmer.language", "kotlin");
        DataSource mockDataSourceDefault = Mockito.mock(DataSource.class);
        DataSource mockDataSourceAnother = Mockito.mock(DataSource.class);
        ApplicationContext ctx = ApplicationContext.run(configMap);
        ctx.registerSingleton(
                DataSource.class, mockDataSourceDefault, Qualifiers.byName("default"));
        ctx.registerSingleton(
                DataSource.class, mockDataSourceAnother, Qualifiers.byName("another"));
        Collection<KSqlClient> sqlClients = ctx.getBeansOfType(KSqlClient.class);
        Assertions.assertEquals(2, sqlClients.size());
        Assertions.assertNotNull(ctx.getBean(KSqlClient.class, Qualifiers.byName("default")));
        Assertions.assertNotNull(ctx.getBean(KSqlClient.class, Qualifiers.byName("another")));
        Assertions.assertThrows(
                io.micronaut.context.exceptions.NoSuchBeanException.class,
                () -> ctx.getBean(JSqlClient.class));
        ctx.close();
    }

    @Test
    void testCacheAbandonedCallbackDefault() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("micronaut.jimmer.language", "java");
        CacheAbandonedCallback mock = Mockito.mock(CacheAbandonedCallback.class);
        ApplicationContext ctx = ApplicationContext.run(configMap);
        ctx.registerSingleton(CacheAbandonedCallback.class, mock);
        CacheAbandonedCallback cacheAbandonedCallback = ctx.getBean(CacheAbandonedCallback.class);
        Assertions.assertNotNull(cacheAbandonedCallback);
        ctx.close();
    }
}
