package io.micronaut.jimmer.java.it.config.jsonmapping;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.inject.Singleton;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.runtime.Customizer;

@Singleton
public class SerializationCustomizer implements Customizer {

    private final ObjectMapper objectMapper;

    public SerializationCustomizer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void customize(JSqlClient.Builder builder) {
        builder.setSerializedTypeObjectMapper(
                AuthUser.class,
                objectMapper
                        .addMixIn(AuthUser.class, AuthUserMixin.class)
                        .enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION));
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}
