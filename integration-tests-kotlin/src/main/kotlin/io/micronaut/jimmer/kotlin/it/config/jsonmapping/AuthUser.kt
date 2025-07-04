package io.micronaut.jimmer.kotlin.it.config.jsonmapping

import org.babyfish.jimmer.sql.Serialized

@Serialized
data class AuthUser(
    val id: String,
    val username: String,
    val password: String,
    val authorities: String,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val credentialsNonExpired: Boolean,
    val enabled: Boolean,
    val tenantId: String,
    val deleteFlag: Boolean,
)
