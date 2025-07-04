package io.micronaut.jimmer.cache.impl;

import io.micronaut.context.Qualifier;
import io.micronaut.context.condition.Condition;
import io.micronaut.context.condition.ConditionContext;
import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.jimmer.cfg.JimmerDataSourceRuntimeConfig;
import org.babyfish.jimmer.sql.event.TriggerType;

@Internal
final class TriggerTypeNotBinlogCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context) {
        BeanDefinition<?> beanDefinition = (BeanDefinition<?>) context.getComponent();
        Qualifier<?> declaredQualifier = beanDefinition.getDeclaredQualifier();
        String dataSourceName =
                declaredQualifier != null ? Qualifiers.findName(declaredQualifier) : null;
        if (null == dataSourceName) {
            return true;
        } else {
            JimmerDataSourceRuntimeConfig runtimeConfig =
                    context.getBean(
                            JimmerDataSourceRuntimeConfig.class, Qualifiers.byName(dataSourceName));
            return !runtimeConfig.getTriggerType().equals(TriggerType.BINLOG_ONLY);
        }
    }
}
