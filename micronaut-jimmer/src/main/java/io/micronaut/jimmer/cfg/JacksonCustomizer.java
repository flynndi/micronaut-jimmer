package io.micronaut.jimmer.cfg;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import jakarta.inject.Singleton;
import org.babyfish.jimmer.jackson.ImmutableModule;

@Factory
@Internal
public class JacksonCustomizer {

    @Singleton
    @Requires(missingBeans = ImmutableModule.class, beans = ObjectMapper.class)
    public ImmutableModule immutableModuleForJackson() {
        return new ImmutableModule();
    }
}
