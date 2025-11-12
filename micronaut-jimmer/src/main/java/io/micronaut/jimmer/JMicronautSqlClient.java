package io.micronaut.jimmer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.aop.InvocationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.PropertyPlaceholderResolver;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.data.connection.jdbc.operations.DefaultDataSourceConnectionOperations;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.jimmer.cfg.JimmerConfiguration;
import io.micronaut.jimmer.cfg.JimmerDataSourceRuntimeConfig;
import io.micronaut.jimmer.cfg.support.MicronautConnectionManager;
import io.micronaut.jimmer.cfg.support.MicronautLogicalDeletedValueGeneratorProvider;
import io.micronaut.jimmer.cfg.support.MicronautTransientResolverProvider;
import io.micronaut.jimmer.cfg.support.MicronautUserIdGeneratorProvider;
import io.micronaut.jimmer.dialect.DialectDetector;
import io.micronaut.jimmer.meta.MicronautMetaStringResolver;
import io.micronaut.transaction.jdbc.DataSourceTransactionManager;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.babyfish.jimmer.impl.util.ObjectUtil;
import org.babyfish.jimmer.sql.DraftInterceptor;
import org.babyfish.jimmer.sql.DraftPreProcessor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.cache.CacheAbandonedCallback;
import org.babyfish.jimmer.sql.cache.CacheFactory;
import org.babyfish.jimmer.sql.cache.CacheOperator;
import org.babyfish.jimmer.sql.di.*;
import org.babyfish.jimmer.sql.dialect.DefaultDialect;
import org.babyfish.jimmer.sql.dialect.Dialect;
import org.babyfish.jimmer.sql.event.*;
import org.babyfish.jimmer.sql.filter.Filter;
import org.babyfish.jimmer.sql.kt.cfg.KCustomizer;
import org.babyfish.jimmer.sql.kt.cfg.KCustomizerKt;
import org.babyfish.jimmer.sql.kt.cfg.KInitializer;
import org.babyfish.jimmer.sql.kt.cfg.KInitializerKt;
import org.babyfish.jimmer.sql.kt.filter.KFilter;
import org.babyfish.jimmer.sql.kt.filter.impl.JavaFiltersKt;
import org.babyfish.jimmer.sql.meta.DatabaseNamingStrategy;
import org.babyfish.jimmer.sql.meta.DatabaseSchemaStrategy;
import org.babyfish.jimmer.sql.meta.DefaultDatabaseSchemaStrategy;
import org.babyfish.jimmer.sql.meta.MetaStringResolver;
import org.babyfish.jimmer.sql.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JMicronautSqlClient extends JLazyInitializationSqlClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMicronautSqlClient.class);

    private final ApplicationContext ctx;

    private final DataSource dataSource;

    private final String dataSourceName;

    private final Consumer<JSqlClient.Builder> block;

    private final boolean isKotlin;

    public JMicronautSqlClient(
            ApplicationContext ctx,
            DataSource dataSource,
            String dataSourceName,
            Consumer<JSqlClient.Builder> block,
            boolean isKotlin) {
        this.ctx = Objects.requireNonNull(ctx, "ApplicationContext must not be null");
        this.dataSource = dataSource;
        this.dataSourceName =
                Objects.requireNonNull(dataSourceName, "dataSourceName must not be null");
        this.block = block;
        this.isKotlin = isKotlin;
    }

    @Override
    protected JSqlClient.Builder createBuilder() {

        JimmerConfiguration properties = getRequiredBean(JimmerConfiguration.class);
        JimmerDataSourceRuntimeConfig runtimeConfig =
                ctx.getBean(JimmerDataSourceRuntimeConfig.class, Qualifiers.byName(dataSourceName));
        UserIdGeneratorProvider userIdGeneratorProvider =
                getOptionalBean(UserIdGeneratorProvider.class);
        LogicalDeletedValueGeneratorProvider logicalDeletedValueGeneratorProvider =
                getOptionalBean(LogicalDeletedValueGeneratorProvider.class);
        TransientResolverProvider transientResolverProvider =
                getOptionalBean(TransientResolverProvider.class);
        AopProxyProvider aopProxyProvider = getOptionalBean(AopProxyProvider.class);
        EntityManager entityManager = getOptionalBean(EntityManager.class);
        DatabaseSchemaStrategy databaseSchemaStrategy =
                getOptionalBean(DatabaseSchemaStrategy.class);
        DatabaseNamingStrategy databaseNamingStrategy =
                getOptionalBean(DatabaseNamingStrategy.class);
        MetaStringResolver metaStringResolver = getOptionalBean(MetaStringResolver.class);
        Dialect dialect = getOptionalBean(Dialect.class);
        DialectDetector dialectDetector = getOptionalBean(DialectDetector.class);
        Executor executor = getOptionalBean(Executor.class);
        SqlFormatter sqlFormatter = getOptionalBean(SqlFormatter.class);
        ObjectMapper objectMapper = getOptionalBean(ObjectMapper.class);
        CacheFactory cacheFactory = getOptionalBean(CacheFactory.class);
        CacheOperator cacheOperator = getOptionalBean(CacheOperator.class, dataSourceName);
        MicroServiceExchange exchange = getOptionalBean(MicroServiceExchange.class);
        Collection<CacheAbandonedCallback> callbacks = getObjects(CacheAbandonedCallback.class);
        Consumer<JSqlClient.Builder> block = getBuilderConsumer();
        Collection<ScalarProvider<?, ?>> providers = getObjects(ScalarProvider.class);
        Collection<PropScalarProviderFactory> factories =
                getObjects(PropScalarProviderFactory.class);
        Collection<DraftPreProcessor<?>> processors = getObjects(DraftPreProcessor.class);
        Collection<DraftInterceptor<?, ?>> interceptors = getObjects(DraftInterceptor.class);
        Collection<ExceptionTranslator<?>> exceptionTranslators =
                getObjects(ExceptionTranslator.class);

        JSqlClient.Builder builder = JSqlClient.newBuilder();
        if (userIdGeneratorProvider != null) {
            builder.setUserIdGeneratorProvider(userIdGeneratorProvider);
        } else {
            builder.setUserIdGeneratorProvider(new MicronautUserIdGeneratorProvider(ctx));
        }
        if (logicalDeletedValueGeneratorProvider != null) {
            builder.setLogicalDeletedValueGeneratorProvider(logicalDeletedValueGeneratorProvider);
        } else {
            builder.setLogicalDeletedValueGeneratorProvider(
                    new MicronautLogicalDeletedValueGeneratorProvider(ctx));
        }
        if (transientResolverProvider != null) {
            builder.setTransientResolverProvider(transientResolverProvider);
        } else {
            builder.setTransientResolverProvider(new MicronautTransientResolverProvider(ctx));
        }
        if (aopProxyProvider != null) {
            builder.setAopProxyProvider(aopProxyProvider);
        } else {
            builder.setAopProxyProvider(this::getTargetClass);
        }
        if (entityManager != null) {
            builder.setEntityManager(entityManager);
        }
        if (databaseSchemaStrategy != null) {
            builder.setDatabaseSchemaStrategy(databaseSchemaStrategy);
        } else if (!runtimeConfig.getDefaultSchema().isEmpty()) {
            builder.setDatabaseSchemaStrategy(
                    new DefaultDatabaseSchemaStrategy(runtimeConfig.getDefaultSchema()));
        }

        builder.setDatabaseSchemaStrategy(
                databaseSchemaStrategy != null
                        ? databaseSchemaStrategy
                        : new DefaultDatabaseSchemaStrategy(runtimeConfig.getDefaultSchema()));

        if (databaseNamingStrategy != null) {
            builder.setDatabaseNamingStrategy(databaseNamingStrategy);
        }
        if (metaStringResolver != null) {
            builder.setMetaStringResolver(metaStringResolver);
        } else {
            builder.setMetaStringResolver(
                    new MicronautMetaStringResolver(
                            getRequiredBean(PropertyPlaceholderResolver.class)));
        }

        builder.setTriggerType(runtimeConfig.getTriggerType());
        builder.setDefaultReferenceFetchType(runtimeConfig.getDefaultReferenceFetchType());
        builder.setMaxJoinFetchDepth(runtimeConfig.getMaxJoinFetchDepth());
        builder.setDefaultDissociateActionCheckable(
                runtimeConfig.isDefaultDissociationActionCheckable());
        builder.setIdOnlyTargetCheckingLevel(runtimeConfig.getIdOnlyTargetCheckingLevel());
        builder.setDefaultEnumStrategy(runtimeConfig.getDefaultEnumStrategy());
        builder.setDefaultBatchSize(runtimeConfig.getDefaultBatchSize());
        builder.setDefaultListBatchSize(runtimeConfig.getDefaultListBatchSize());
        builder.setInListPaddingEnabled(runtimeConfig.isInListPaddingEnabled());
        builder.setExpandedInListPaddingEnabled(runtimeConfig.isExpandedInListPaddingEnabled());
        builder.setDissociationLogicalDeleteEnabled(
                runtimeConfig.isDissociationLogicalDeleteEnabled());
        builder.setOffsetOptimizingThreshold(runtimeConfig.getOffsetOptimizingThreshold());
        builder.setReverseSortOptimizationEnabled(runtimeConfig.isReverseSortOptimizationEnabled());
        builder.setForeignKeyEnabledByDefault(runtimeConfig.isForeignKeyEnabledByDefault());
        builder.setMaxCommandJoinCount(runtimeConfig.getMaxCommandJoinCount());
        builder.setMutationTransactionRequired(runtimeConfig.isMutationTransactionRequired());
        builder.setTargetTransferable(runtimeConfig.isTargetTransferable());
        builder.setExplicitBatchEnabled(runtimeConfig.isExplicitBatchEnabled());
        builder.setDumbBatchAcceptable(runtimeConfig.isDumbBatchAcceptable());
        builder.setConstraintViolationTranslatable(
                runtimeConfig.isConstraintViolationTranslatable());
        builder.setExecutorContextPrefixes(runtimeConfig.getExecutorContextPrefixes());
        if (runtimeConfig.isShowSql()) {
            builder.setExecutor(Executor.log(executor));
        } else {
            builder.setExecutor(executor);
        }
        if (sqlFormatter != null) {
            builder.setSqlFormatter(sqlFormatter);
        } else if (runtimeConfig.isPrettySql()) {
            if (runtimeConfig.isInlineSqlVariables()) {
                builder.setSqlFormatter(SqlFormatter.INLINE_PRETTY);
            } else {
                builder.setSqlFormatter(SqlFormatter.PRETTY);
            }
        }
        builder.setDatabaseValidationMode(runtimeConfig.getDatabaseValidation().getMode())
                .setDefaultSerializedTypeObjectMapper(objectMapper)
                .setCacheFactory(cacheFactory)
                .setCacheOperator(cacheOperator)
                .addCacheAbandonedCallbacks(callbacks);

        for (ScalarProvider<?, ?> provider : providers) {
            builder.addScalarProvider(provider);
        }
        for (PropScalarProviderFactory factory : factories) {
            builder.addPropScalarProviderFactory(factory);
        }

        builder.addDraftPreProcessors(processors);
        builder.addDraftInterceptors(interceptors);
        builder.addExceptionTranslators(exceptionTranslators);
        initializeByLanguage(builder);
        builder.addInitializers(new MicronautEventInitializer(ctx));

        builder.setMicroServiceName(properties.getMicroServiceName());
        if (!properties.getMicroServiceName().isEmpty()) {
            builder.setMicroServiceExchange(exchange);
        }

        if (null != this.block) {
            this.block.accept(builder);
        }
        if (null != block) {
            block.accept(builder);
        }

        ConnectionManager connectionManager =
                ObjectUtil.firstNonNullOf(
                        () -> ((JSqlClientImplementor.Builder) builder).getConnectionManager(),
                        () -> getOptionalBean(ConnectionManager.class),
                        () ->
                                new MicronautConnectionManager(
                                        getRequiredBean(
                                                DefaultDataSourceConnectionOperations.class,
                                                dataSourceName),
                                        () -> getOptionalBean(DataSourceTransactionManager.class)));

        builder.setConnectionManager(connectionManager);

        if (((Builder) builder).getDialect().getClass() == DefaultDialect.class) {
            DialectDetector finalDetector =
                    dialectDetector != null
                            ? dialectDetector
                            : new DialectDetector.Impl(dataSource);
            builder.setDialect(
                    ObjectUtil.optionalFirstNonNullOf(
                            () -> dialect,
                            runtimeConfig::getDialect,
                            () -> connectionManager.execute(finalDetector::detectDialect)));
        }

        return builder;
    }

    private void initializeByLanguage(JSqlClient.Builder builder) {

        Collection<Filter<?>> javaFilters = getObjects(Filter.class);
        Collection<Customizer> javaCustomizers = getObjects(Customizer.class);
        Collection<Initializer> javaInitializers = getObjects(Initializer.class);
        Collection<KFilter<?>> kotlinFilters = getObjects(KFilter.class);
        Collection<KCustomizer> kotlinCustomizers = getObjects(KCustomizer.class);
        Collection<KInitializer> kotlinInitializers = getObjects(KInitializer.class);

        if (isKotlin) {
            if (!javaFilters.isEmpty()) {
                LOGGER.warn(
                        "Jimmer is working in kotlin mode, but some java filters "
                                + "has been found in micronaut context, they will be ignored");
            }
            if (!javaCustomizers.isEmpty()) {
                LOGGER.warn(
                        "Jimmer is working in kotlin mode, but some java customizers "
                                + "has been found in micronaut context, they will be ignored");
            }
            if (!javaInitializers.isEmpty()) {
                LOGGER.warn(
                        "Jimmer is working in kotlin mode, but some java initializers "
                                + "has been found in micronaut context, they will be ignored");
            }
            builder.addFilters(
                    kotlinFilters.stream()
                            .map(JavaFiltersKt::toJavaFilter)
                            .collect(Collectors.toList()));
            builder.addCustomizers(
                    kotlinCustomizers.stream()
                            .map(KCustomizerKt::toJavaCustomizer)
                            .collect(Collectors.toList()));
            builder.addInitializers(
                    kotlinInitializers.stream()
                            .map(KInitializerKt::toJavaInitializer)
                            .collect(Collectors.toList()));
        } else {
            if (!kotlinFilters.isEmpty()) {
                LOGGER.warn(
                        "Jimmer is working in java mode, but some kotlin filters "
                                + "has been found in micronaut context, they will be ignored");
            }
            if (!kotlinCustomizers.isEmpty()) {
                LOGGER.warn(
                        "Jimmer is working in java mode, but some kotlin customizers "
                                + "has been found in micronaut context, they will be ignored");
            }
            if (!kotlinInitializers.isEmpty()) {
                LOGGER.warn(
                        "Jimmer is working in kotlin mode, but some kotlin initializers "
                                + "has been found in micronaut context, they will be ignored");
            }
            builder.addFilters(javaFilters);
            builder.addCustomizers(javaCustomizers);
            builder.addInitializers(javaInitializers);
        }
    }

    private static class MicronautEventInitializer implements Initializer {

        private final ApplicationEventPublisher<EntityEvent<?>> entityEventPublisher;

        private final ApplicationEventPublisher<AssociationEvent> associationPublisher;

        private MicronautEventInitializer(ApplicationContext ctx) {
            this.entityEventPublisher =
                    ctx.getBean(Argument.of(ApplicationEventPublisher.class, EntityEvent.class));
            this.associationPublisher =
                    ctx.getBean(
                            Argument.of(ApplicationEventPublisher.class, AssociationEvent.class));
        }

        @Override
        public void initialize(JSqlClient sqlClient) {
            Triggers[] triggersArr =
                    ((JSqlClientImplementor) sqlClient).getTriggerType() == TriggerType.BOTH
                            ? new Triggers[] {sqlClient.getTriggers(), sqlClient.getTriggers(true)}
                            : new Triggers[] {sqlClient.getTriggers()};
            for (Triggers triggers : triggersArr) {
                triggers.addEntityListener(entityEventPublisher::publishEvent);
                triggers.addAssociationListener(associationPublisher::publishEvent);
            }
        }
    }

    private <T> T getRequiredBean(Class<T> type) {
        return ctx.getBean(type);
    }

    private <T> T getRequiredBean(Class<T> type, String dataSourceName) {
        return ctx.getBean(type, Qualifiers.byName(dataSourceName));
    }

    private <T> T getOptionalBean(Class<T> type) {
        if (ctx.findBean(type).isPresent()) {
            return ctx.getBean(type);
        } else if (ctx.findBean(type, Qualifiers.byName(dataSourceName)).isPresent()) {
            return ctx.getBean(type, Qualifiers.byName(dataSourceName));
        } else {
            return null;
        }
    }

    private <T> T getOptionalBean(Class<T> type, String dataSourceName) {
        if (ctx.findBean(type, Qualifiers.byName(dataSourceName)).isPresent()) {
            return ctx.getBean(type, Qualifiers.byName(dataSourceName));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> Collection<T> getObjects(Class<?> beanType) {
        Collection<?> beansOfType = ctx.getBeansOfType(beanType, Qualifiers.byName(dataSourceName));
        if (!CollectionUtils.isEmpty(beansOfType)) {
            return (Collection<T>) beansOfType;
        }
        return (Collection<T>) ctx.getBeansOfType(beanType);
    }

    @SuppressWarnings("unchecked")
    private Consumer<JSqlClient.Builder> getBuilderConsumer() {
        if (ctx.findBean(
                        Argument.of(Consumer.class, JSqlClient.Builder.class),
                        Qualifiers.byName(dataSourceName))
                .isPresent()) {
            return ctx.getBean(
                    Argument.of(Consumer.class, JSqlClient.Builder.class),
                    Qualifiers.byName(dataSourceName));
        } else if (ctx.findBean(Argument.of(Consumer.class, JSqlClient.Builder.class))
                .isPresent()) {
            return ctx.getBean(Argument.of(Consumer.class, JSqlClient.Builder.class));
        } else {
            return null;
        }
    }

    private Class<?> getTargetClass(Object instance) {
        if (instance instanceof InvocationContext) {
            return ((InvocationContext<?, ?>) instance).getTarget().getClass();
        } else {
            return instance.getClass();
        }
    }
}
