package io.micronaut.jimmer.java.it.resource;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpStatus;
import io.micronaut.jimmer.java.it.event.TestChangeEventObserves;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MicronautTest
public class TestEvent {

    @Inject TestChangeEventObserves testChangeEventObserves;

    @Inject RequestSpecification requestSpecification;

    @BeforeEach
    void clearEvents() {
        testChangeEventObserves.getEntityEventStorage().clear();
        testChangeEventObserves.getAssociationEventStorageOne().clear();
        testChangeEventObserves.getAssociationEventStorageTwo().clear();
    }

    @Test
    void testEvent() {
        String body =
                """
                {
                    "id": 55,
                    "name": "mergeInput",
                    "edition": 1,
                    "price": "10.00",
                    "tenant": "d",
                    "storeId": 1,
                    "authors": [
                        {
                            "id": 11,
                            "firstName": "mergeInput",
                            "lastName": "mergeInput",
                            "gender": "FEMALE"
                        }
                    ]
                }
                """;
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE,
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryMergeInput");
        Assertions.assertFalse(testChangeEventObserves.getEntityEventStorage().isEmpty());
        Assertions.assertFalse(testChangeEventObserves.getAssociationEventStorageOne().isEmpty());
        Assertions.assertFalse(testChangeEventObserves.getAssociationEventStorageTwo().isEmpty());
        Assertions.assertEquals(1, testChangeEventObserves.getEntityEventStorage().size());
        Assertions.assertEquals(1, testChangeEventObserves.getAssociationEventStorageOne().size());
        Assertions.assertEquals(1, testChangeEventObserves.getAssociationEventStorageTwo().size());
    }

    @Test
    void testEvent2() {
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE,
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .log()
                        .all()
                        .when()
                        .post("testResources/testEvent");
        Assertions.assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        Assertions.assertEquals(1, testChangeEventObserves.getEntityEventStorage().size());
        Assertions.assertEquals(1, testChangeEventObserves.getAssociationEventStorageOne().size());
        Assertions.assertEquals(2, testChangeEventObserves.getAssociationEventStorageTwo().size());
    }
}
