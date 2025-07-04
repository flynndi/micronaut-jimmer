package io.micronaut.jimmer.kotlin.it.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.annotation.RequestBean
import io.micronaut.jimmer.kotlin.it.entity.UserRole
import io.micronaut.jimmer.kotlin.it.entity.dto.UserRoleSpecification
import io.micronaut.jimmer.kotlin.it.repository.UserRoleRepository
import io.micronaut.jimmer.kotlin.it.service.IUserRoleService
import jakarta.transaction.Transactional
import java.util.UUID

@Controller("/userRoleResources")
open class UserRoleResources(
    private val iUserRoleService: IUserRoleService,
    private val userRoleRepository: UserRoleRepository,
) {
    @Get("/userRoleFindById")
    fun userRoleFindById(
        @QueryValue id: UUID,
    ): HttpResponse<UserRole> = HttpResponse.ok(iUserRoleService.findById(id))

    @Put("/updateUserRoleById")
    @Transactional(rollbackOn = [Exception::class])
    open fun updateUserRoleById(
        @QueryValue id: UUID,
    ): HttpResponse<Void> {
        iUserRoleService.updateById(id)
        return HttpResponse.ok()
    }

    @Delete("/delete")
    @Transactional(rollbackOn = [Exception::class])
    open fun delete(
        @QueryValue id: UUID,
    ): HttpResponse<Void> {
        iUserRoleService.deleteById(id)
        return HttpResponse.ok()
    }

    @Get("/deleteReverseById")
    fun deleteReverseById(
        @QueryValue id: UUID,
    ): HttpResponse<UserRole> = HttpResponse.ok(iUserRoleService.deleteReverseById(id))

    @Get("/testUserRoleSpecification")
    fun testUserRoleSpecification(
        @RequestBean userRoleSpecification: UserRoleSpecification,
    ): HttpResponse<List<UserRole>> = HttpResponse.ok(userRoleRepository.find(userRoleSpecification))
}
