package io.micronaut.jimmer.kotlin.it.entity

import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface TenantAware : BaseEntity {
    val tenant: String
}
