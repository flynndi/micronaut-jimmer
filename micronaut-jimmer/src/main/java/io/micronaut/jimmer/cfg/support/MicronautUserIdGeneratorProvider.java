package io.micronaut.jimmer.cfg.support;

import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import java.util.Collection;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.di.DefaultUserIdGeneratorProvider;
import org.babyfish.jimmer.sql.meta.UserIdGenerator;

public class MicronautUserIdGeneratorProvider extends DefaultUserIdGeneratorProvider {

    private final ApplicationContext ctx;

    public MicronautUserIdGeneratorProvider(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public UserIdGenerator<?> get(Class<UserIdGenerator<?>> type, JSqlClient sqlClient)
            throws Exception {
        Collection<UserIdGenerator<?>> userIdGenerators = ctx.getBeansOfType(type);
        if (userIdGenerators.isEmpty()) {
            return super.get(type, sqlClient);
        }
        if (userIdGenerators.size() > 1) {
            throw new IllegalStateException(
                    "Two many micronaut beans whose type is \"" + type.getName() + "\"");
        }
        return userIdGenerators.iterator().next();
    }

    @Override
    public UserIdGenerator<?> get(String ref, JSqlClient sqlClient) {
        UserIdGenerator userIdGenerator =
                ctx.getBean(UserIdGenerator.class, Qualifiers.byName(ref));
        if (null != userIdGenerator) {
            return userIdGenerator;
        } else {
            throw new IllegalStateException(
                    "The expected type of micronaut bean named \""
                            + ref
                            + "\" is \""
                            + UserIdGenerator.class.getName()
                            + "\", but the actual type is not found");
        }
    }
}
