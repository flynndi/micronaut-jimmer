package io.micronaut.jimmer.java.it.resource;

import io.micronaut.jimmer.java.it.Constant;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class UserRoleResourcesTestCase {

    @Inject RequestSpecification requestSpecification;

    @Test
    public void testUserRole() {
        Response response =
                requestSpecification
                        .queryParam("id", Constant.USER_ROLE_ID)
                        .log()
                        .all()
                        .when()
                        .get("userRoleResources/userRoleFindById");
        Assertions.assertEquals(Constant.USER_ROLE_ID, response.jsonPath().getString("id"));
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"));
    }

    @Test
    public void testUpdateUserRole() {
        Response response =
                requestSpecification
                        .queryParam("id", Constant.USER_ROLE_ID)
                        .log()
                        .all()
                        .when()
                        .put("userRoleResources/updateUserRoleById");
        Assertions.assertEquals(HttpStatus.SC_OK, response.statusCode());
    }

    @Test
    public void testUserRoleSpecification() {
        Response response =
                requestSpecification
                        .queryParam("userId", Constant.USER_ID)
                        .queryParam("roleId", Constant.ROLE_ID)
                        .log()
                        .all()
                        .when()
                        .get("userRoleResources/testUserRoleSpecification");
        Assertions.assertEquals(HttpStatus.SC_OK, response.statusCode());
        Assertions.assertEquals(Constant.USER_ID, response.jsonPath().getString("[0].userId"));
    }
}
