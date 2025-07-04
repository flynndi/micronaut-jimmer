package io.micronaut.jimmer.kotlin.it.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.RedisClient
import io.micronaut.context.annotation.Factory
import io.micronaut.jimmer.cache.RedisCacheCreator
import jakarta.inject.Singleton
import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.meta.ImmutableType
import org.babyfish.jimmer.sql.cache.AbstractCacheFactory
import org.babyfish.jimmer.sql.cache.Cache
import org.babyfish.jimmer.sql.cache.CacheFactory
import org.babyfish.jimmer.sql.cache.redisson.RedissonCacheLocker
import org.babyfish.jimmer.sql.cache.redisson.RedissonCacheTracker
import org.redisson.api.RedissonClient
import java.time.Duration

@Factory
class CacheConfig {
    @Singleton
    fun cacheFactory(
        redissonClient: RedissonClient,
        redisClient: RedisClient?,
        objectMapper: ObjectMapper?,
    ): CacheFactory {
        val creator =
            RedisCacheCreator(redisClient, objectMapper)
                .withRemoteDuration(Duration.ofHours(1))
                .withLocalCache(100, Duration.ofMinutes(5))
                .withMultiViewProperties(40, Duration.ofMinutes(2), Duration.ofMinutes(24))
                .withSoftLock(
                    RedissonCacheLocker(redissonClient),
                    Duration.ofSeconds(30),
                ).withTracking(RedissonCacheTracker(redissonClient))

        return object : AbstractCacheFactory() {
            override fun createObjectCache(type: ImmutableType?): Cache<*, *>? = creator.createForObject<Any?, Any?>(type)

            override fun createAssociatedIdCache(prop: ImmutableProp): Cache<*, *>? =
                creator.createForProp<Any?, Any?>(
                    prop,
                    filterState.isAffected(prop.targetType),
                )

            override fun createAssociatedIdListCache(prop: ImmutableProp): Cache<*, MutableList<*>?>? =
                creator.createForProp<Any?, MutableList<*>?>(
                    prop,
                    filterState.isAffected(prop.targetType),
                )

            override fun createResolverCache(prop: ImmutableProp?): Cache<*, *>? = creator.createForProp<Any?, Any?>(prop, true)
        }
    }
}
