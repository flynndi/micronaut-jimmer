package io.micronaut.jimmer.kotlin.it.resource

import io.micronaut.context.ApplicationContext
import io.micronaut.jimmer.kotlin.it.repository.TreeNodeRepository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
class TreeNodeResourcesTestCase {
    @Inject
    lateinit var treeNodeRepository: TreeNodeRepository

    @Inject
    lateinit var requestSpecification: RequestSpecification

    @Inject
    lateinit var applicationContext: ApplicationContext

    @Test
    fun testTreeNodeRepository() {
        val treeNodeRepository: TreeNodeRepository? =
            applicationContext.getBean(TreeNodeRepository::class.java)
        Assertions.assertEquals(treeNodeRepository, this.treeNodeRepository)
    }

    @Test
    fun testInfiniteRecursion() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("treeNodeResources/infiniteRecursion")
        Assertions.assertEquals(1, response.jsonPath().getInt("[0].id"))
        Assertions.assertEquals(2, response.jsonPath().getList<Any?>("[0].childNodes").size)
    }

    @Test
    fun testAll() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("treeNodeResources/all")
        Assertions.assertEquals(24, response.jsonPath().getList<Any>("").size)
        Assertions.assertEquals(2, response.jsonPath().getList<Any>("[0].childNodes").size)
        Assertions.assertEquals(9, response.jsonPath().getInt("[0].childNodes[0].id"))
        Assertions.assertEquals(
            2,
            response.jsonPath().getList<Any>("[0].childNodes[0].childNodes").size,
        )
    }
}
