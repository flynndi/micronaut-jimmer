package io.micronaut.jimmer.kotlin.it.service

import io.micronaut.jimmer.kotlin.it.entity.UserRole
import java.util.UUID

interface IUserRoleService {
    fun findById(id: UUID): UserRole?

    fun updateById(id: UUID)

    fun deleteById(id: UUID)

    fun deleteReverseById(id: UUID): UserRole?
}
