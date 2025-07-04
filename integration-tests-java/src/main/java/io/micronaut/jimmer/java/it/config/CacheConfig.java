package io.micronaut.jimmer.java.it.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.micronaut.context.annotation.Factory;
import io.micronaut.jimmer.cache.RedisCacheCreator;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.List;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.sql.cache.AbstractCacheFactory;
import org.babyfish.jimmer.sql.cache.Cache;
import org.babyfish.jimmer.sql.cache.CacheCreator;
import org.babyfish.jimmer.sql.cache.CacheFactory;
import org.babyfish.jimmer.sql.cache.redisson.RedissonCacheLocker;
import org.babyfish.jimmer.sql.cache.redisson.RedissonCacheTracker;
import org.redisson.api.RedissonClient;

@Factory
public class CacheConfig {

    @Singleton
    public CacheFactory cacheFactory(
            RedissonClient redissonClient, RedisClient redisClient, ObjectMapper objectMapper) {
        CacheCreator creator =
                new RedisCacheCreator(redisClient, objectMapper)
                        .withRemoteDuration(Duration.ofHours(1))
                        .withLocalCache(100, Duration.ofMinutes(5))
                        .withMultiViewProperties(40, Duration.ofMinutes(2), Duration.ofMinutes(24))
                        .withSoftLock(
                                new RedissonCacheLocker(redissonClient), Duration.ofSeconds(30))
                        .withTracking(new RedissonCacheTracker(redissonClient));

        return new AbstractCacheFactory() {

            @Override
            public Cache<?, ?> createObjectCache(ImmutableType type) {
                return creator.createForObject(type);
            }

            @Override
            public Cache<?, ?> createAssociatedIdCache(ImmutableProp prop) {
                return creator.createForProp(
                        prop, getFilterState().isAffected(prop.getTargetType()));
            }

            @Override
            public Cache<?, List<?>> createAssociatedIdListCache(ImmutableProp prop) {
                return creator.createForProp(
                        prop, getFilterState().isAffected(prop.getTargetType()));
            }

            @Override
            public Cache<?, ?> createResolverCache(ImmutableProp prop) {
                return creator.createForProp(prop, true);
            }
        };
    }
}
