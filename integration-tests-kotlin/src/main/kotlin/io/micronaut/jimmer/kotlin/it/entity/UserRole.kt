package io.micronaut.jimmer.kotlin.it.entity

import io.micronaut.jimmer.kotlin.it.config.UUIdGenerator
import io.micronaut.jimmer.kotlin.it.config.jsonmapping.AuthUser
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.LogicalDeleted
import java.util.UUID

@Entity
interface UserRole {
    @Id
    @GeneratedValue(generatorType = UUIdGenerator::class)
    val id: UUID

    val userId: String

    val roleId: String

    @LogicalDeleted("true")
    val deleteFlag: Boolean

    val authUser: AuthUser?
}
