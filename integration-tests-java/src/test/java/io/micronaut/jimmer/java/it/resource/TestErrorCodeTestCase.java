package io.micronaut.jimmer.java.it.resource;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class TestErrorCodeTestCase {

    @Inject RequestSpecification requestSpecification;

    @Test
    void testHelloEndpoint() {
        Response response = requestSpecification.when().get("/bookResource/testError");
        Assertions.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.statusCode());
        Assertions.assertEquals("USER_INFO", response.body().jsonPath().getString("family"));
        Assertions.assertEquals("ILLEGAL_USER_NAME", response.body().jsonPath().getString("code"));
        Assertions.assertEquals("[a]", response.body().jsonPath().getString("illegalChars"));
    }
}
