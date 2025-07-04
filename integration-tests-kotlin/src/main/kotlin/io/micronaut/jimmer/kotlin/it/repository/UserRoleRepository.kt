package io.micronaut.jimmer.kotlin.it.repository

import io.micronaut.jimmer.kotlin.it.entity.UserRole
import io.micronaut.jimmer.kotlin.it.entity.by
import io.micronaut.jimmer.kotlin.it.entity.dto.UserRoleSpecification
import io.micronaut.jimmer.repository.KRepository
import io.micronaut.jimmer.repository.annotation.Repository
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import java.util.UUID

@Repository(dataSourceName = "DB2")
interface UserRoleRepository : KRepository<UserRole, UUID> {
    fun find(userRoleSpecification: UserRoleSpecification): List<UserRole> =
        sql
            .createQuery(UserRole::class) {
                where(userRoleSpecification)
                select(table.fetch(newFetcher(UserRole::class).by { allScalarFields() }))
            }.execute()

    fun findByUserId(userId: String): UserRole

    fun findByRoleId(roleId: String): UserRole

    fun findByUserIdAndRoleId(
        userId: String,
        roleId: String,
    ): UserRole
}
