package io.micronaut.jimmer.kotlin.it.repository

import io.micronaut.jimmer.kotlin.it.config.Constant
import io.micronaut.jimmer.kotlin.it.entity.UserRole
import io.micronaut.jimmer.repo.support.AbstractKotlinRepository
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.babyfish.jimmer.sql.kt.KSqlClient
import java.util.UUID

@Singleton
class UserRoleKotlinRepository(
    @Named(Constant.DATASOURCE2) kSqlClient: KSqlClient,
) : AbstractKotlinRepository<UserRole, UUID>(kSqlClient)
