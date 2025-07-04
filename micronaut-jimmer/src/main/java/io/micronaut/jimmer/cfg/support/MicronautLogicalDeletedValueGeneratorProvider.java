package io.micronaut.jimmer.cfg.support;

import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.di.LogicalDeletedValueGeneratorProvider;
import org.babyfish.jimmer.sql.meta.LogicalDeletedValueGenerator;

public class MicronautLogicalDeletedValueGeneratorProvider
        implements LogicalDeletedValueGeneratorProvider {

    private final ApplicationContext ctx;

    public MicronautLogicalDeletedValueGeneratorProvider(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public LogicalDeletedValueGenerator<?> get(String ref, JSqlClient sqlClient) throws Exception {
        LogicalDeletedValueGenerator logicalDeletedValueGenerator =
                ctx.getBean(LogicalDeletedValueGenerator.class, Qualifiers.byName(ref));
        if (null != logicalDeletedValueGenerator) {
            return logicalDeletedValueGenerator;
        } else {
            throw new IllegalStateException(
                    "The expected type of micronaut bean named \""
                            + ref
                            + "\" is \""
                            + LogicalDeletedValueGenerator.class.getName()
                            + "\", but the actual type is not found");
        }
    }
}
