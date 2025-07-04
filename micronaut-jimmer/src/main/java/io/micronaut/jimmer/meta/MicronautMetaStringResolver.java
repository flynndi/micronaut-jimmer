package io.micronaut.jimmer.meta;

import io.micronaut.context.env.PropertyPlaceholderResolver;
import org.babyfish.jimmer.sql.meta.MetaStringResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MicronautMetaStringResolver implements MetaStringResolver {

    private final PropertyPlaceholderResolver propertyPlaceholderResolver;

    public MicronautMetaStringResolver(PropertyPlaceholderResolver propertyPlaceholderResolver) {
        this.propertyPlaceholderResolver = propertyPlaceholderResolver;
    }

    @Nullable
    @Override
    public String resolve(@NotNull String value) {
        return propertyPlaceholderResolver.resolveRequiredPlaceholders(value);
    }
}
