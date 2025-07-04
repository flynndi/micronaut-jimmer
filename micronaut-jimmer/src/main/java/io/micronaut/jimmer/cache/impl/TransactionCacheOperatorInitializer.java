package io.micronaut.jimmer.cache.impl;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.order.Ordered;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import java.util.Optional;
import javax.sql.DataSource;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.cache.TransactionCacheOperator;
import org.babyfish.jimmer.sql.kt.KSqlClient;

@Singleton
@Internal
@EachBean(DataSource.class)
@Requires(condition = TriggerTypeNotBinlogCondition.class)
final class TransactionCacheOperatorInitializer
        implements ApplicationEventListener<StartupEvent>, Ordered {

    public final String dataSourceName;

    TransactionCacheOperatorInitializer(@Parameter String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        BeanContext beanContext = event.getSource();
        Optional<JSqlClient> optionalJSqlClient =
                beanContext.findBean(JSqlClient.class, Qualifiers.byName(dataSourceName));
        if (optionalJSqlClient.isPresent()) {
            TransactionCacheOperator transactionCacheOperator = new TransactionCacheOperator();
            beanContext.registerSingleton(
                    TransactionCacheOperator.class,
                    transactionCacheOperator,
                    Qualifiers.byName(dataSourceName),
                    false);
            transactionCacheOperator.initialize(optionalJSqlClient.get());
        }
        Optional<KSqlClient> optionalKSqlClient =
                beanContext.findBean(KSqlClient.class, Qualifiers.byName(dataSourceName));
        if (optionalKSqlClient.isPresent()) {
            TransactionCacheOperator transactionCacheOperator = new TransactionCacheOperator();
            beanContext.registerSingleton(
                    TransactionCacheOperator.class,
                    transactionCacheOperator,
                    Qualifiers.byName(dataSourceName),
                    false);
            transactionCacheOperator.initialize(optionalKSqlClient.get().getJavaClient());
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
