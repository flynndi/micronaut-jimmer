package io.micronaut.jimmer.java.it.config;

import io.micronaut.jimmer.java.it.entity.TenantAwareProps;
import jakarta.inject.Singleton;
import org.babyfish.jimmer.sql.filter.Filter;
import org.babyfish.jimmer.sql.filter.FilterArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TenantFilter implements Filter<TenantAwareProps> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public void filter(FilterArgs<TenantAwareProps> args) {
        LOGGER.info("args: {}", args);
        LOGGER.info("args.getTable(): {}", args.getTable());
        args.where(args.getTable().tenant().eq("a"));
    }
}
