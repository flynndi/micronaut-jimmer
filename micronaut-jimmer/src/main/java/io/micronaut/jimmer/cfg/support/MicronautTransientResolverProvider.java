package io.micronaut.jimmer.cfg.support;

import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.TransientResolver;
import org.babyfish.jimmer.sql.di.TransientResolverProvider;

public class MicronautTransientResolverProvider implements TransientResolverProvider {

    private final ApplicationContext ctx;

    public MicronautTransientResolverProvider(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public TransientResolver<?, ?> get(Class<TransientResolver<?, ?>> type, JSqlClient sqlClient)
            throws Exception {
        return ctx.getBean(type);
    }

    @Override
    public TransientResolver<?, ?> get(String ref, JSqlClient sqlClient) throws Exception {
        TransientResolver transientResolver =
                ctx.getBean(TransientResolver.class, Qualifiers.byName(ref));
        if (null != transientResolver) {
            return transientResolver;
        } else {
            throw new IllegalStateException(
                    "The expected type of micronaut bean named \""
                            + ref
                            + "\" is \""
                            + TransientResolver.class.getName()
                            + "\", but the actual type is not found");
        }
    }
}
