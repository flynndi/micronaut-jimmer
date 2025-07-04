package io.micronaut.jimmer.kotlin.it.config.jsonmapping

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import jakarta.inject.Singleton
import org.babyfish.jimmer.sql.kt.cfg.KCustomizer
import org.babyfish.jimmer.sql.kt.cfg.KSqlClientDsl

@Singleton
class SerializationCustomizer(
    private val objectMapper: ObjectMapper,
) : KCustomizer {
    override fun customize(dsl: KSqlClientDsl) {
        dsl
            .setSerializedTypeObjectMapper(
                AuthUser::class,
                objectMapper
                    .addMixIn(AuthUser::class.java, AuthUserMixin::class.java)
                    .enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION),
            )
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    }
}
