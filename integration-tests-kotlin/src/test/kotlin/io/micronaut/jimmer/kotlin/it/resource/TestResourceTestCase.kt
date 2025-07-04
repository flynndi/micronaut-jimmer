package io.micronaut.jimmer.kotlin.it.resource

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.ApplicationContext
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpStatus
import io.micronaut.jimmer.kotlin.it.entity.UserRole
import io.micronaut.jimmer.kotlin.it.repository.BookRepository
import io.micronaut.jimmer.kotlin.it.repository.BookStoreRepository
import io.micronaut.jimmer.kotlin.it.repository.UserRoleRepository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.netty.handler.codec.http.HttpHeaderValues
import io.restassured.http.Header
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

@MicronautTest
class TestResourceTestCase {
    @Inject
    lateinit var bookRepository: BookRepository

    @Inject
    lateinit var userRoleRepository: UserRoleRepository

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var requestSpecification: RequestSpecification

    @Inject
    lateinit var applicationContext: ApplicationContext

    @Test
    fun testRepository() {
        val bookRepository: BookRepository? =
            applicationContext.getBean(BookRepository::class.java)
        val bookStoreRepository: BookStoreRepository =
            applicationContext.getBean(BookStoreRepository::class.java)
        val userRoleRepository: UserRoleRepository? =
            applicationContext.getBean(UserRoleRepository::class.java)
        Assertions.assertNotNull(bookRepository)
        Assertions.assertNotNull(bookStoreRepository)
        Assertions.assertEquals(bookRepository, this.bookRepository)
        Assertions.assertEquals(userRoleRepository, this.userRoleRepository)
    }

    @Test
    fun testPage() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1))
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
        val response =
            requestSpecification
                .body(body)
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryPage")
        val responseJsonPath = response.jsonPath()
        Assertions.assertEquals(6, responseJsonPath.getInt("totalSize"))
        Assertions.assertEquals(6, responseJsonPath.getInt("totalPages"))
    }

    @Test
    fun testPageOther() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1))
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
        val response =
            requestSpecification
                .body(body)
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryPageOther")
        val responseJsonPath = response.jsonPath()
        Assertions.assertEquals(6, responseJsonPath.getInt("totalSize"))
        Assertions.assertEquals(6, responseJsonPath.getInt("totalPages"))
    }

    @Test
    fun testBookRepositoryPageSort() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1))
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
        val response =
            requestSpecification
                .body(body)
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryPageSort")
        val responseJsonPath = response.jsonPath()
        Assertions.assertEquals(6, responseJsonPath.getInt("totalSize"))
        Assertions.assertEquals(6, responseJsonPath.getInt("totalPages"))
        Assertions.assertEquals(11, responseJsonPath.getLong("content[0].id"))
    }

    @Test
    fun testPageFetcher() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1))
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
        val response =
            requestSpecification
                .body(body)
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryPageFetcher")
        val responseJsonPath = response.jsonPath()
        Assertions.assertEquals(6, responseJsonPath.getInt("totalSize"))
        Assertions.assertEquals(6, responseJsonPath.getInt("totalPages"))
        Assertions.assertNotNull(responseJsonPath.getList<Any?>("content"))
        Assertions.assertNotNull(responseJsonPath.get<Any?>("content.authors"))
    }

    @Test
    fun testBookRepositoryById() {
        val response =
            requestSpecification
                .queryParam("id", 1L)
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryById")
        Assertions.assertNotNull(response.jsonPath())
    }

    @Test
    fun testBookRepositoryByIdOptionalPresent() {
        val response =
            requestSpecification
                .queryParam("id", 1L)
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryByIdOptional")
        Assertions.assertNotNull(response.jsonPath())
    }

    @Test
    fun testBookRepositoryByIdOptionalEmpty() {
        val response =
            requestSpecification
                .queryParam("id", 0)
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryByIdOptional")
        Assertions.assertEquals(HttpStatus.NO_CONTENT.code, response.statusCode())
    }

    @Test
    fun testBookRepositoryByIdFetcher() {
        val response =
            requestSpecification
                .queryParam("id", 0)
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryByIdFetcher")
        Assertions.assertEquals(HttpStatus.NOT_FOUND.code, response.statusCode())
    }

    @Test
    fun testBookRepositoryByIdFetcherOptionalPresent() {
        val response =
            requestSpecification
                .queryParam("id", 1L)
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryByIdFetcherOptional")
        Assertions.assertNotNull(response.jsonPath())
    }

    @Test
    fun testBookRepositoryByIdFetcherOptionalEmpty() {
        val response =
            requestSpecification
                .queryParam("id", 0)
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryByIdFetcherOptional")
        Assertions.assertEquals(HttpStatus.NOT_FOUND.code, response.statusCode())
    }

    @Test
    fun testBookRepositoryViewById() {
        val response =
            requestSpecification
                .queryParam("id", 1L)
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryViewById")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(1, response.jsonPath().getLong("id"))
        Assertions.assertNotNull(response.jsonPath().getJsonObject<Any?>("store"))
        Assertions.assertNotNull(response.jsonPath().getJsonObject<Any?>("authors"))
    }

    @Test
    fun testBookRepositoryFindAllById() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(mutableListOf<Int?>(1, 2))
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindAllById")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(1, response.jsonPath().getLong("[0].id"))
    }

    @Test
    fun testBookRepositoryFindByIdsFetcher() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(mutableListOf<Int?>(1, 2))
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindByIdsFetcher")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(1, response.jsonPath().getLong("[0].id"))
        Assertions.assertEquals(2, response.jsonPath().getLong("[0].authors[0].id"))
    }

    @Test
    fun testBookRepositoryFindMapByIds() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(mutableListOf<Int?>(1, 2))
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindMapByIds")
        Assertions.assertNotNull(response.jsonPath().getMap<Any?, Any?>(""))
    }

    @Test
    fun testBookRepositoryFindMapByIdsFetcher() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(mutableListOf<Int?>(1, 2))
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindMapByIdsFetcher")
        Assertions.assertNotNull(response.jsonPath().getMap<Any?, Any?>(""))
        Assertions.assertNotNull(response.jsonPath().getMap<Any?, Any?>("").get("1"))
    }

    @Test
    fun testBookRepositoryFindAll() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryFindAll")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(1, response.jsonPath().getLong("[0].id"))
    }

    @Test
    fun testBookRepositoryFindAllTypedPropScalar() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryFindAllTypedPropScalar")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(
            "Programming TypeScript",
            response.jsonPath().getString("[0].name"),
        )
    }

    @Test
    fun testBookRepositoryFindAllFetcherTypedPropScalar() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryFindAllFetcherTypedPropScalar")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(
            "Programming TypeScript",
            response.jsonPath().getString("[0].name"),
        )
        Assertions.assertNotNull(response.jsonPath().getString("[0].authors"))
        Assertions.assertNotNull(response.jsonPath().getString("[0].store"))
    }

    @Test
    fun testBookRepositoryFindAllSort() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryFindAllSort")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(
            "Programming TypeScript",
            response.jsonPath().getString("[0].name"),
        )
    }

    @Test
    fun testBookRepositoryFindAllFetcherSort() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryFindAllFetcherSort")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(
            "Programming TypeScript",
            response.jsonPath().getString("[0].name"),
        )
        Assertions.assertNotNull(response.jsonPath().getString("[0].authors"))
        Assertions.assertNotNull(response.jsonPath().getString("[0].store"))
    }

    @Test
    fun testBookRepositoryFindAllPageFetcher() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1))
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindAllPageFetcher")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"))
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"))
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"))
    }

    @Test
    fun testBookRepositoryFindAllPageTypedPropScalar() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1))
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
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
                .post("testResources/testBookRepositoryFindAllPageTypedPropScalar")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertNull(response.jsonPath().getString("content[0].authors"))
        Assertions.assertEquals(
            "Programming TypeScript",
            response.jsonPath().getString("content[0].name"),
        )
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"))
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"))
    }

    @Test
    fun testBookRepositoryFindAllPageFetcherTypedPropScalar() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1))
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
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
                .post("testResources/testBookRepositoryFindAllPageFetcherTypedPropScalar")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"))
        Assertions.assertEquals(
            "Programming TypeScript",
            response.jsonPath().getString("content[0].name"),
        )
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"))
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"))
    }

    @Test
    fun testBookRepositoryFindAllPageSort() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1))
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindAllPageSort")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(
            "Programming TypeScript",
            response.jsonPath().getString("content[0].name"),
        )
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"))
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"))
    }

    @Test
    fun testBookRepositoryFindAllPageFetcherSort() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1))
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindAllPageFetcherSort")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(
            "Programming TypeScript",
            response.jsonPath().getString("content[0].name"),
        )
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"))
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"))
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"))
    }

    @Test
    fun testBookRepositoryExistsById() {
        val response =
            requestSpecification
                .queryParam("id", 0)
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryExistsById")
        Assertions.assertFalse(response.jsonPath().getBoolean(""))
    }

    @Test
    fun testBookRepositoryCount() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryCount")
        Assertions.assertEquals(6, response.jsonPath().getInt(""))
    }

    @Test
    fun testUserRoleRepositoryInsert() {
        val body: String
        val id = UUID.randomUUID()
        val userId: String = UUID.randomUUID().toString()
        val roleId: String = UUID.randomUUID().toString()
        val userRole =
            UserRole {
                this.id = id
                this.userId = userId
                this.roleId = roleId
            }
        try {
            body = objectMapper.writeValueAsString(userRole)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
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
                .post("testResources/testUserRoleRepositoryInsert")
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"))
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"))
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"))
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"))
    }

    fun testUserRoleRepositoryInsertInput() {
        val body: String
        val id = UUID.randomUUID()
        val userId: String = UUID.randomUUID().toString()
        val roleId: String = UUID.randomUUID().toString()
        val userRole =
            UserRole {
                this.id = id
                this.userId = userId
                this.roleId = roleId
            }
        try {
            body = objectMapper.writeValueAsString(userRole)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testUserRoleRepositoryInsertInput")
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"))
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"))
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"))
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"))
    }

    @Test
    fun testUserRoleRepositorySave() {
        val body: String
        val id = UUID.randomUUID()
        val userId: String = UUID.randomUUID().toString()
        val roleId: String = UUID.randomUUID().toString()
        val userRole =
            UserRole {
                this.id = id
                this.userId = userId
                this.roleId = roleId
            }
        try {
            body = objectMapper.writeValueAsString(userRole)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
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
                .post("testResources/testUserRoleRepositorySave")
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"))
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"))
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"))
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"))
    }

    fun testUserRoleRepositorySaveInput() {
        val body: String
        val id = UUID.randomUUID()
        val userId: String = UUID.randomUUID().toString()
        val roleId: String = UUID.randomUUID().toString()
        val userRole =
            UserRole {
                this.id = id
                this.userId = userId
                this.roleId = roleId
            }
        try {
            body = objectMapper.writeValueAsString(userRole)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
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
                .post("testResources/testUserRoleRepositorySaveInput")
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"))
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"))
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"))
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"))
    }

    fun testUserRoleRepositorySaveInputSaveMode() {
        val body: String
        val id = UUID.randomUUID()
        val userId: String = UUID.randomUUID().toString()
        val roleId: String = UUID.randomUUID().toString()
        val userRole =
            UserRole {
                this.id = id
                this.userId = userId
                this.roleId = roleId
            }
        try {
            body = objectMapper.writeValueAsString(userRole)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
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
                .post("testResources/testUserRoleRepositorySaveInputSaveMode")
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"))
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"))
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"))
    }

    fun testUserRoleRepositorySaveCommand() {
        val body: String
        val id = UUID.randomUUID()
        val userId: String = UUID.randomUUID().toString()
        val roleId: String = UUID.randomUUID().toString()
        val userRole =
            UserRole {
                this.id = id
                this.userId = userId
                this.roleId = roleId
            }
        try {
            body = objectMapper.writeValueAsString(userRole)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
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
                .post("testResources/testUserRoleRepositorySaveCommand")
        Assertions.assertEquals(HttpStatus.OK.code, response.statusCode())
    }

    @Test
    fun testUserRoleRepositorySaveEntities() {
        val body: String
        val id1 = UUID.randomUUID()
        val userId1: String = UUID.randomUUID().toString()
        val roleId1: String = UUID.randomUUID().toString()
        val userRole1 =
            UserRole {
                this.id = id1
                this.userId = userId1
                this.roleId = roleId1
            }
        val id2 = UUID.randomUUID()
        val userId2: String = UUID.randomUUID().toString()
        val roleId2: String = UUID.randomUUID().toString()
        val userRole2 =
            UserRole {
                this.id = id2
                this.userId = userId2
                this.roleId = roleId2
            }
        try {
            body = objectMapper.writeValueAsString(arrayOf(userRole1, userRole2))
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testUserRoleRepositorySaveEntities")
        Assertions.assertEquals(HttpStatus.OK.code, response.statusCode())
        Assertions.assertEquals(id1.toString(), response.jsonPath().getString("[0].id"))
        Assertions.assertEquals(id2.toString(), response.jsonPath().getString("[1].id"))
    }

    @Test
    fun testUserRoleRepositorySaveEntitiesSaveMode() {
        val body: String
        val id1 = UUID.randomUUID()
        val userId1: String = UUID.randomUUID().toString()
        val roleId1: String = UUID.randomUUID().toString()
        val userRole1 =
            UserRole {
                this.id = id1
                this.userId = userId1
                this.roleId = roleId1
            }
        val id2 = UUID.randomUUID()
        val userId2: String = UUID.randomUUID().toString()
        val roleId2: String = UUID.randomUUID().toString()
        val userRole2 =
            UserRole {
                this.id = id2
                this.userId = userId2
                this.roleId = roleId2
            }
        try {
            body = objectMapper.writeValueAsString(arrayOf(userRole1, userRole2))
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
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
                .post("testResources/testUserRoleRepositorySaveEntitiesSaveMode")
        Assertions.assertEquals(HttpStatus.OK.code, response.statusCode())
        Assertions.assertEquals(id1.toString(), response.jsonPath().getString("[0].id"))
        Assertions.assertEquals(userId1, response.jsonPath().getString("[0].userId"))
        Assertions.assertEquals(roleId1, response.jsonPath().getString("[0].roleId"))
        Assertions.assertEquals(id2.toString(), response.jsonPath().getString("[1].id"))
        Assertions.assertEquals(userId2, response.jsonPath().getString("[1].userId"))
        Assertions.assertEquals(roleId2, response.jsonPath().getString("[1].roleId"))
    }

    @Test
    fun testUserRoleRepositorySaveEntitiesCommand() {
        val body: String
        val id1 = UUID.randomUUID()
        val userId1: String = UUID.randomUUID().toString()
        val roleId1: String = UUID.randomUUID().toString()
        val userRole1 =
            UserRole {
                this.id = id1
                this.userId = userId1
                this.roleId = roleId1
            }
        val id2 = UUID.randomUUID()
        val userId2: String = UUID.randomUUID().toString()
        val roleId2: String = UUID.randomUUID().toString()
        val userRole2 =
            UserRole {
                this.id = id2
                this.userId = userId2
                this.roleId = roleId2
            }
        try {
            body = objectMapper.writeValueAsString(arrayOf(userRole1, userRole2))
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testUserRoleRepositorySaveEntitiesCommand")
        Assertions.assertEquals(HttpStatus.OK.code, response.statusCode())
    }

    @Test
    fun testUserRoleRepositoryUpdate() {
        val body: String
        val id = UUID.randomUUID()
        val userId: String = UUID.randomUUID().toString()
        val roleId: String = UUID.randomUUID().toString()
        val userRole =
            UserRole {
                this.id = id
                this.userId = userId
                this.roleId = roleId
            }
        try {
            body = objectMapper.writeValueAsString(userRole)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testUserRoleRepositoryUpdate")
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"))
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"))
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"))
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"))
    }

    @Test
    fun testUserRoleRepositoryById() {
        val response =
            requestSpecification
                .queryParam("id", UUID.fromString("defc2d01-fb38-4d31-b006-fd182b25aa33"))
                .log()
                .all()
                .`when`()
                .get("testResources/testUserRoleRepositoryById")
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(
            "defc2d01-fb38-4d31-b006-fd182b25aa33",
            response.jsonPath().getString("id"),
        )
    }

    fun testUserRoleRepositoryUpdateInput() {
        val body: String
        val id = UUID.randomUUID()
        val userId: String = UUID.randomUUID().toString()
        val roleId: String = UUID.randomUUID().toString()
        val userRole =
            UserRole {
                this.id = id
                this.userId = userId
                this.roleId = roleId
            }
        try {
            body = objectMapper.writeValueAsString(userRole)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .body(body)
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).log()
                .all()
                .`when`()
                .put("testResources/testUserRoleRepositoryUpdateInput")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
    }

    @Test
    fun testBookRepositoryFindByIdsView() {
        val body: String
        val ids: MutableList<Long> = ArrayList<Long>()
        ids.add(1L)
        ids.add(3L)
        ids.add(5L)
        ids.add(7L)
        try {
            body = objectMapper.writeValueAsString(ids)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .body(body)
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindByIdsView")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
        Assertions.assertNotNull(response.jsonPath().get("[0].store"))
        Assertions.assertNotNull(response.jsonPath().get("[0].authors"))
    }

    @Test
    fun testBookRepositoryFindAllView() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryFindAllView")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
        Assertions.assertNotNull(response.jsonPath().get<Any?>("[0].store"))
        Assertions.assertNotNull(response.jsonPath().get<Any?>("[0].authors"))
    }

    @Test
    fun testBookRepositoryFindAllTypedPropScalarView() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryFindAllTypedPropScalarView")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
        Assertions.assertEquals(9, response.jsonPath().getLong("[0].id"))
        Assertions.assertNotNull(response.jsonPath().get<Any?>("[0].store"))
        Assertions.assertNotNull(response.jsonPath().get<Any?>("[0].authors"))
    }

    @Test
    fun testBookRepositoryFindAllSortView() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryFindAllSortView")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
        Assertions.assertEquals(9, response.jsonPath().getLong("[0].id"))
        Assertions.assertNotNull(response.jsonPath().get<Any?>("[0].store"))
        Assertions.assertNotNull(response.jsonPath().get<Any?>("[0].authors"))
    }

    @Test
    fun testBookRepositoryFindAllPageView() {
        val body: String?
        val pageable = Pageable.from(0, 1)
        try {
            body = objectMapper.writeValueAsString(pageable)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindAllPageView")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(
            "Learning GraphQL",
            response.jsonPath().getString("content[0].name"),
        )
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"))
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"))
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"))
    }

    @Test
    fun testBookRepositoryFindAllPageTypedPropScalarView() {
        val body: String?
        val pageable = Pageable.from(0, 1)
        try {
            body = objectMapper.writeValueAsString(pageable)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindAllPageTypedPropScalarView")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(
            "Programming TypeScript",
            response.jsonPath().getString("content[0].name"),
        )
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"))
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"))
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"))
    }

    @Test
    fun testBookRepositoryFindAllPageSortView() {
        val body: String?
        val pageable = Pageable.from(0, 1)
        try {
            body = objectMapper.writeValueAsString(pageable)
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindAllPageSortView")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(
            "Programming TypeScript",
            response.jsonPath().getString("content[0].name"),
        )
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"))
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"))
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"))
    }

    @Test
    fun testBookRepositoryCustomQuery() {
        val response =
            requestSpecification
                .queryParam("id", 1L)
                .log()
                .all()
                .`when`()
                .get("testResources/testBookRepositoryCustomQuery")
        Assertions.assertNotNull(response.jsonPath())
    }

    @Test
    fun testBookRepositoryFindMapByIdsView() {
        val body: String?
        try {
            body = objectMapper.writeValueAsString(mutableListOf<Int?>(1, 2))
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException(e)
        }
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryFindMapByIdsView")
        Assertions.assertNotNull(response.jsonPath().getMap<Any?, Any?>(""))
        Assertions.assertNotNull(response.jsonPath().get<Any?>("1"))
    }

    fun testBookRepositoryMerge() {
        val body =
            """
            {
                "id": 22,
                "name": "merge",
                "edition": 1,
                "price": "10.00",
                "tenant": "c",
                "store": {
                    "id": 6,
                    "name": "mergeStore",
                    "website": "mergeWebsite"
                },
                "authors": [
                    {
                        "id": 10,
                        "firstName": "merge",
                        "lastName": "merge",
                        "gender": "FEMALE"
                    }
                ]
            }
            
            """.trimIndent()
        val response =
            requestSpecification
                .header(
                    Header(
                        HttpHeaders.CONTENT_TYPE.toString(),
                        HttpHeaderValues.APPLICATION_JSON.toString(),
                    ),
                ).body(body)
                .log()
                .all()
                .`when`()
                .post("testResources/testBookRepositoryMerge")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
    }

    fun testBookRepositoryMergeInput() {
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
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
    }

    @Test
    fun testBookRepositoryMergeSaveMode() {
        val body =
            """
            {
                "id": 77,
                "name": "mergeSaveMode",
                "edition": 1,
                "price": "10.00",
                "tenant": "c",
                "store": {
                    "id": 10,
                    "name": "mergeSaveMode",
                    "website": "mergeSaveMode"
                },
                "authors": [
                    {
                        "id": 20,
                        "firstName": "mergeSaveMode",
                        "lastName": "mergeSaveMode",
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
                .post("testResources/testBookRepositoryMergeSaveMode")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
    }

//    @Test
    fun testMicronautOrdersSortUtilsStringCodes() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("testResources/testMicronautOrdersSortUtilsStringCodes")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(11, response.jsonPath().getInt("[0].id"))
    }

//    @Test
    fun testMicronautOrdersSortUtilsTypedPropScalarProps() {
        val response =
            requestSpecification
                .log()
                .all()
                .`when`()
                .get("testResources/testMicronautOrdersSortUtilsTypedPropScalarProps")
        Assertions.assertEquals(HttpStatus.OK.code, response.getStatusCode())
        Assertions.assertNotNull(response.jsonPath())
        Assertions.assertEquals(1, response.jsonPath().getInt("[0].id"))
    }
}
