package io.micronaut.jimmer.kotlin.it.resource

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpStatus
import io.micronaut.jimmer.kotlin.it.event.TestChangeEventObserves
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.netty.handler.codec.http.HttpHeaderValues
import io.restassured.http.Header
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@MicronautTest
class TestEvent {
    @Inject
    lateinit var testChangeEventObserves: TestChangeEventObserves

    @Inject
    lateinit var requestSpecification: RequestSpecification

    @BeforeEach
    fun clearEvents() {
        testChangeEventObserves.getEntityEventStorage().clear()
        testChangeEventObserves.getAssociationEventStorageOne().clear()
        testChangeEventObserves.getAssociationEventStorageTwo().clear()
    }

    fun testEvent() {
        val body =
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
            
            """.trimIndent()
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryMergeInput")
        Assertions.assertFalse(testChangeEventObserves.getEntityEventStorage().isEmpty())
        Assertions.assertFalse(testChangeEventObserves.getAssociationEventStorageOne().isEmpty())
        Assertions.assertFalse(testChangeEventObserves.getAssociationEventStorageTwo().isEmpty())
        Assertions.assertEquals(1, testChangeEventObserves.getEntityEventStorage().size)
        Assertions.assertEquals(1, testChangeEventObserves.getAssociationEventStorageOne().size)
        Assertions.assertEquals(1, testChangeEventObserves.getAssociationEventStorageTwo().size)
    }

    @Test
    fun testEvent2() {
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).log()
                .all()
                .`when`()
                .post("testResources/testEvent")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
        Assertions.assertEquals(1, testChangeEventObserves.getEntityEventStorage().size)
        Assertions.assertEquals(1, testChangeEventObserves.getAssociationEventStorageOne().size)
        Assertions.assertEquals(2, testChangeEventObserves.getAssociationEventStorageTwo().size)
    }
}
