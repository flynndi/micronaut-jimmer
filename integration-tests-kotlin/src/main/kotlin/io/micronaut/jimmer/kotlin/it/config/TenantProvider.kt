package io.micronaut.jimmer.kotlin.it.config

import jakarta.inject.Singleton

@Singleton
class TenantProvider {
    val tenant: String
        get() {
            return "a"
        }
}
