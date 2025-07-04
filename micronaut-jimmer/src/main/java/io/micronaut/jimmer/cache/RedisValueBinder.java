package io.micronaut.jimmer.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import java.time.Duration;
import java.util.*;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.sql.cache.CacheTracker;
import org.babyfish.jimmer.sql.cache.spi.AbstractRemoteValueBinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedisValueBinder<K, V> extends AbstractRemoteValueBinder<K, V> {

    private final RedisCommands<String, byte[]> commands;

    protected RedisValueBinder(
            @Nullable ImmutableType type,
            @Nullable ImmutableProp prop,
            @Nullable CacheTracker tracker,
            @Nullable ObjectMapper objectMapper,
            Duration duration,
            int randomPercent,
            @NotNull RedisClient redisClient) {
        super(type, prop, tracker, objectMapper, duration, randomPercent);
        StatefulRedisConnection<String, byte[]> connect =
                redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
        commands = connect.sync();
    }

    @Override
    protected List<byte[]> read(Collection<String> keys) {
        return this.multiGet(keys, commands);
    }

    @Override
    protected void write(Map<String, byte[]> map) {
        commands.mset(map);
        for (String key : map.keySet()) {
            commands.pexpire(key, nextExpireMillis());
        }
    }

    @Override
    protected void deleteAllSerializedKeys(List<String> serializedKeys) {
        String[] array = serializedKeys.toArray(serializedKeys.toArray(new String[0]));
        commands.del(array);
    }

    @Override
    protected boolean matched(@Nullable Object reason) {
        return "redis".equals(reason);
    }

    private List<byte[]> multiGet(Collection<String> keys, RedisCommands<String, byte[]> commands) {
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        String[] array = keys.toArray(keys.toArray(new String[0]));
        List<KeyValue<String, byte[]>> mGet = commands.mget(array);
        List<byte[]> result = new ArrayList<>(mGet.size());
        for (KeyValue<String, byte[]> stringKeyValue : mGet) {
            if (stringKeyValue.hasValue()) {
                result.add(stringKeyValue.getValue());
            }
        }
        return result;
    }

    @NotNull
    public static <K, V> Builder<K, V> forObject(ImmutableType type) {
        return new Builder<>(type, null);
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

        public RedisValueBinder.Builder<K, V> redis(RedisClient redisClient) {
            this.redisClient = redisClient;
            return this;
        }

        public RedisValueBinder<K, V> build() {
            if (null == redisClient) {
                throw new IllegalStateException("redisClient has not been specified");
            }
            return new RedisValueBinder<>(
                    type, prop, tracker, objectMapper, duration, randomPercent, redisClient);
        }
    }
}
