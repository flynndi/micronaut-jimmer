package io.micronaut.jimmer.kotlin.it.config

import org.babyfish.jimmer.sql.meta.UserIdGenerator
import java.util.UUID

class UUIdGenerator : UserIdGenerator<UUID> {
    override fun generate(entityType: Class<*>?): UUID = UUID.randomUUID()
}
