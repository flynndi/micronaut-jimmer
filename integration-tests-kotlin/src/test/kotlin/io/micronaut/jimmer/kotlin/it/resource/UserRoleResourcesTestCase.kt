package io.micronaut.jimmer.kotlin.it.resource

import io.micronaut.http.HttpStatus
import io.micronaut.jimmer.kotlin.it.Constant
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
class UserRoleResourcesTestCase {
    @Inject
    lateinit var requestSpecification: RequestSpecification

    @Test
    fun testUserRole() {
        val response =
            requestSpecification
                .queryParam("id", Constant.USER_ROLE_ID)
                .log()
                .all()
                .`when`()
                .get("userRoleResources/userRoleFindById")
        Assertions.assertEquals(Constant.USER_ROLE_ID, response.jsonPath().getString("id"))
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"))
    }

    @Test
    fun testUpdateUserRole() {
        val response =
            requestSpecification
                .queryParam("id", Constant.USER_ROLE_ID)
                .log()
                .all()
                .`when`()
                .put("userRoleResources/updateUserRoleById")
        Assertions.assertEquals(HttpStatus.OK.code, response.statusCode())
    }

    fun testUserRoleSpecification() {
        val response =
            requestSpecification
                .queryParam("userId", Constant.USER_ID)
                .queryParam("roleId", Constant.ROLE_ID)
                .log()
                .all()
                .`when`()
                .get("userRoleResources/testUserRoleSpecification")
        Assertions.assertEquals(HttpStatus.OK.code, response.statusCode())
        Assertions.assertEquals(
            Constant.USER_ID,
            response.jsonPath().getString("[0].userId"),
        )
    }
}
