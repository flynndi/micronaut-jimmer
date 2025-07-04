package io.micronaut.jimmer.kotlin.it.service.impl

import io.micronaut.jimmer.kotlin.it.config.Constant
import io.micronaut.jimmer.kotlin.it.entity.UserRole
import io.micronaut.jimmer.kotlin.it.entity.id
import io.micronaut.jimmer.kotlin.it.entity.roleId
import io.micronaut.jimmer.kotlin.it.repository.UserRoleRepository
import io.micronaut.jimmer.kotlin.it.service.IUserRoleService
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.runtime.LogicalDeletedBehavior
import java.util.UUID

@Singleton
open class UserRoleServiceImpl(
    @Named(Constant.DATASOURCE2) private val sqlClient: KSqlClient,
    private val userRoleRepository: UserRoleRepository,
) : IUserRoleService {
    override fun findById(id: UUID): UserRole? = sqlClient.findById(UserRole::class, id)

    @Transactional(rollbackFor = [Exception::class])
    override fun updateById(id: UUID) {
        sqlClient
            .createUpdate(UserRole::class) {
                set(table.roleId, "123")
                where(table.id.eq(id))
            }.execute()
    }

    override fun deleteById(id: UUID) {
        userRoleRepository.deleteById(id)
    }

    override fun deleteReverseById(id: UUID): UserRole? =
        sqlClient
            .filters {
                setBehavior(LogicalDeletedBehavior.REVERSED)
            }.findById(UserRole::class, id)
}
