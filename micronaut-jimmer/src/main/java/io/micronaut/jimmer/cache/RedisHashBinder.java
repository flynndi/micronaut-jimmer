package io.micronaut.jimmer.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisHashCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.sql.cache.CacheTracker;
import org.babyfish.jimmer.sql.cache.RemoteKeyPrefixProvider;
import org.babyfish.jimmer.sql.cache.spi.AbstractRemoteHashBinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedisHashBinder<K, V> extends AbstractRemoteHashBinder<K, V> {

    private final RedisHashCommands<String, byte[]> redisHashCommands;

    private final RedisCommands<String, byte[]> redisCommands;

    protected RedisHashBinder(
            @Nullable ImmutableType type,
            @Nullable ImmutableProp prop,
            @Nullable CacheTracker tracker,
            @Nullable ObjectMapper objectMapper,
            @Nullable RemoteKeyPrefixProvider keyPrefixProvider,
            @NotNull Duration duration,
            int randomPercent,
            @NotNull RedisClient redisClient) {
        super(type, prop, tracker, objectMapper, keyPrefixProvider, duration, randomPercent);
        StatefulRedisConnection<String, byte[]> connect =
                redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
        redisHashCommands = connect.sync();
        redisCommands = connect.sync();
    }

    @Override
    protected List<byte[]> read(Collection<String> keys, String hashKey) {
        if (keys.isEmpty()) {
            return null;
        }
        return keys.stream()
                .map(key -> redisHashCommands.hget(key, hashKey))
                .collect(Collectors.toList());
    }

    @Override
    protected void write(Map<String, byte[]> map, String hashKey) {
        for (Map.Entry<String, byte[]> entry : map.entrySet()) {
            String key = entry.getKey();
            byte[] value = entry.getValue();
            redisHashCommands.hset(key, hashKey, value);
            redisHashCommands.hpexpire(key, nextExpireMillis());
        }
    }

    @Override
    protected void deleteAllSerializedKeys(List<String> serializedKeys) {
        for (String key : serializedKeys) {
            redisCommands.del(key);
        }
    }

    @Override
    protected boolean matched(@Nullable Object reason) {
        return "redis".equals(reason);
    }

    @NotNull
    public static <K, V> Builder<K, V> forProp(ImmutableProp prop) {
        return new Builder<>(null, prop);
    }

    public static class Builder<K, V> extends AbstractBuilder<K, V, Builder<K, V>> {

        private RedisClient redisClient;

        protected Builder(ImmutableType type, ImmutableProp prop) {
            super(type, prop);
        }

        public Builder<K, V> redis(RedisClient redisClient) {
            this.redisClient = redisClient;
            return this;
        }

        public RedisHashBinder<K, V> build() {
            if (null == redisClient) {
                throw new IllegalStateException("redisClient has not been specified");
            }
            return new RedisHashBinder<>(
                    type,
                    prop,
                    tracker,
                    objectMapper,
                    keyPrefixProvider,
                    duration,
                    randomPercent,
                    redisClient);
        }
    }
}
