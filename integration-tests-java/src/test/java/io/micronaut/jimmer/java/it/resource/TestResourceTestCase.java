package io.micronaut.jimmer.java.it.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.ApplicationContext;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.jimmer.java.it.entity.UserRole;
import io.micronaut.jimmer.java.it.entity.UserRoleDraft;
import io.micronaut.jimmer.java.it.repository.BookRepository;
import io.micronaut.jimmer.java.it.repository.BookStoreRepository;
import io.micronaut.jimmer.java.it.repository.UserRoleRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class TestResourceTestCase {

    @Inject BookRepository bookRepository;

    @Inject UserRoleRepository userRoleRepository;

    @Inject ObjectMapper objectMapper;

    @Inject RequestSpecification requestSpecification;

    @Inject ApplicationContext applicationContext;

    @Test
    void testRepository() {
        BookRepository bookRepository = applicationContext.getBean(BookRepository.class);
        BookStoreRepository bookStoreRepository =
                applicationContext.getBean(BookStoreRepository.class);
        UserRoleRepository userRoleRepository =
                applicationContext.getBean(UserRoleRepository.class);
        Assertions.assertNotNull(bookRepository);
        Assertions.assertNotNull(bookStoreRepository);
        Assertions.assertEquals(bookRepository, this.bookRepository);
        Assertions.assertEquals(userRoleRepository, this.userRoleRepository);
    }

    @Test
    void testPage() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .body(body)
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE,
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryPage");
        JsonPath responseJsonPath = response.jsonPath();
        Assertions.assertEquals(6, responseJsonPath.getInt("totalSize"));
        Assertions.assertEquals(6, responseJsonPath.getInt("totalPages"));
    }

    @Test
    void testPageOther() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .body(body)
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryPageOther");
        JsonPath responseJsonPath = response.jsonPath();
        Assertions.assertEquals(6, responseJsonPath.getInt("totalSize"));
        Assertions.assertEquals(6, responseJsonPath.getInt("totalPages"));
    }

    @Test
    void testBookRepositoryPageSort() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .body(body)
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryPageSort");
        JsonPath responseJsonPath = response.jsonPath();
        Assertions.assertEquals(6, responseJsonPath.getInt("totalSize"));
        Assertions.assertEquals(6, responseJsonPath.getInt("totalPages"));
        Assertions.assertEquals(11, responseJsonPath.getLong("content[0].id"));
    }

    @Test
    void testPageFetcher() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .body(body)
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryPageFetcher");
        JsonPath responseJsonPath = response.jsonPath();
        Assertions.assertEquals(6, responseJsonPath.getInt("totalSize"));
        Assertions.assertEquals(6, responseJsonPath.getInt("totalPages"));
        Assertions.assertNotNull(responseJsonPath.getList("content"));
        Assertions.assertNotNull(responseJsonPath.get("content.authors"));
    }

    @Test
    void testBookRepositoryById() {
        Response response =
                requestSpecification
                        .queryParam("id", 1L)
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryById");
        Assertions.assertNotNull(response.jsonPath());
    }

    @Test
    void testBookRepositoryByIdOptionalPresent() {
        Response response =
                requestSpecification
                        .queryParam("id", 1L)
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryByIdOptional");
        Assertions.assertNotNull(response.jsonPath());
    }

    @Test
    void testBookRepositoryByIdOptionalEmpty() {
        Response response =
                requestSpecification
                        .queryParam("id", 0)
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryByIdOptional");
        Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, response.statusCode());
    }

    @Test
    void testBookRepositoryByIdFetcher() {
        Response response =
                requestSpecification
                        .queryParam("id", 0)
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryByIdFetcher");
        Assertions.assertEquals(HttpStatus.SC_NOT_FOUND, response.statusCode());
    }

    @Test
    void testBookRepositoryByIdFetcherOptionalPresent() {
        Response response =
                requestSpecification
                        .queryParam("id", 1L)
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryByIdFetcherOptional");
        Assertions.assertNotNull(response.jsonPath());
    }

    @Test
    void testBookRepositoryByIdFetcherOptionalEmpty() {
        Response response =
                requestSpecification
                        .queryParam("id", 0)
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryByIdFetcherOptional");
        Assertions.assertEquals(HttpStatus.SC_NOT_FOUND, response.statusCode());
    }

    @Test
    void testBookRepositoryViewById() {
        Response response =
                requestSpecification
                        .queryParam("id", 1L)
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryViewById");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(1, response.jsonPath().getLong("id"));
        Assertions.assertNotNull(response.jsonPath().getJsonObject("store"));
        Assertions.assertNotNull(response.jsonPath().getJsonObject("authors"));
    }

    @Test
    void testBookRepositoryFindAllById() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Arrays.asList(1, 2));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindAllById");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(1, response.jsonPath().getLong("[0].id"));
    }

    @Test
    void testBookRepositoryFindByIdsFetcher() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Arrays.asList(1, 2));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindByIdsFetcher");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(1, response.jsonPath().getLong("[0].id"));
        Assertions.assertEquals(2, response.jsonPath().getLong("[0].authors[0].id"));
    }

    @Test
    void testBookRepositoryFindMapByIds() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Arrays.asList(1, 2));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindMapByIds");
        Assertions.assertNotNull(response.jsonPath().getMap(""));
    }

    @Test
    void testBookRepositoryFindMapByIdsFetcher() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Arrays.asList(1, 2));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindMapByIdsFetcher");
        Assertions.assertNotNull(response.jsonPath().getMap(""));
        Assertions.assertNotNull(response.jsonPath().getMap("").get("1"));
    }

    @Test
    void testBookRepositoryFindAll() {
        Response response =
                requestSpecification
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryFindAll");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(1, response.jsonPath().getLong("[0].id"));
    }

    @Test
    void testBookRepositoryFindAllTypedPropScalar() {
        Response response =
                requestSpecification
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryFindAllTypedPropScalar");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(
                "Programming TypeScript", response.jsonPath().getString("[0].name"));
    }

    @Test
    void testBookRepositoryFindAllFetcherTypedPropScalar() {
        Response response =
                requestSpecification
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryFindAllFetcherTypedPropScalar");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(
                "Programming TypeScript", response.jsonPath().getString("[0].name"));
        Assertions.assertNotNull(response.jsonPath().getString("[0].authors"));
        Assertions.assertNotNull(response.jsonPath().getString("[0].store"));
    }

    @Test
    void testBookRepositoryFindAllSort() {
        Response response =
                requestSpecification
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryFindAllSort");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(
                "Programming TypeScript", response.jsonPath().getString("[0].name"));
    }

    @Test
    void testBookRepositoryFindAllFetcherSort() {
        Response response =
                requestSpecification
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryFindAllFetcherSort");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(
                "Programming TypeScript", response.jsonPath().getString("[0].name"));
        Assertions.assertNotNull(response.jsonPath().getString("[0].authors"));
        Assertions.assertNotNull(response.jsonPath().getString("[0].store"));
    }

    @Test
    void testBookRepositoryFindAllPageFetcher() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindAllPageFetcher");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"));
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"));
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"));
    }

    @Test
    void testBookRepositoryFindAllPageTypedPropScalar() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindAllPageTypedPropScalar");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertNull(response.jsonPath().getString("content[0].authors"));
        Assertions.assertEquals(
                "Programming TypeScript", response.jsonPath().getString("content[0].name"));
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"));
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"));
    }

    @Test
    void testBookRepositoryFindAllPageFetcherTypedPropScalar() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindAllPageFetcherTypedPropScalar");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"));
        Assertions.assertEquals(
                "Programming TypeScript", response.jsonPath().getString("content[0].name"));
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"));
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"));
    }

    @Test
    void testBookRepositoryFindAllPageSort() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindAllPageSort");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(
                "Programming TypeScript", response.jsonPath().getString("content[0].name"));
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"));
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"));
    }

    @Test
    void testBookRepositoryFindAllPageFetcherSort() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Pageable.from(0, 1));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindAllPageFetcherSort");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(
                "Programming TypeScript", response.jsonPath().getString("content[0].name"));
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"));
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"));
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"));
    }

    @Test
    void testBookRepositoryExistsById() {
        Response response =
                requestSpecification
                        .queryParam("id", 0)
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryExistsById");
        Assertions.assertFalse(response.jsonPath().getBoolean(""));
    }

    @Test
    void testBookRepositoryCount() {
        Response response =
                requestSpecification
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryCount");
        Assertions.assertEquals(6, response.jsonPath().getInt(""));
    }

    @Test
    void testUserRoleRepositoryInsert() {
        String body;
        UUID id = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();
        String roleId = UUID.randomUUID().toString();
        UserRole userRole =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id);
                            draft.setUserId(userId);
                            draft.setRoleId(roleId);
                        });
        try {
            body = objectMapper.writeValueAsString(userRole);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testUserRoleRepositoryInsert");
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"));
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"));
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"));
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"));
    }

    @Test
    void testUserRoleRepositoryInsertInput() {
        String body;
        UUID id = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();
        String roleId = UUID.randomUUID().toString();
        UserRole userRole =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id);
                            draft.setUserId(userId);
                            draft.setRoleId(roleId);
                        });
        try {
            body = objectMapper.writeValueAsString(userRole);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testUserRoleRepositoryInsertInput");
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"));
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"));
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"));
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"));
    }

    @Test
    void testUserRoleRepositorySave() {
        String body;
        UUID id = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();
        String roleId = UUID.randomUUID().toString();
        UserRole userRole =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id);
                            draft.setUserId(userId);
                            draft.setRoleId(roleId);
                        });
        try {
            body = objectMapper.writeValueAsString(userRole);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testUserRoleRepositorySave");
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"));
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"));
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"));
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"));
    }

    @Test
    void testUserRoleRepositorySaveInput() {
        String body;
        UUID id = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();
        String roleId = UUID.randomUUID().toString();
        UserRole userRole =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id);
                            draft.setUserId(userId);
                            draft.setRoleId(roleId);
                        });
        try {
            body = objectMapper.writeValueAsString(userRole);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testUserRoleRepositorySaveInput");
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"));
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"));
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"));
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"));
    }

    @Test
    void testUserRoleRepositorySaveInputSaveMode() {
        String body;
        UUID id = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();
        String roleId = UUID.randomUUID().toString();
        UserRole userRole =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id);
                            draft.setUserId(userId);
                            draft.setRoleId(roleId);
                        });
        try {
            body = objectMapper.writeValueAsString(userRole);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testUserRoleRepositorySaveInputSaveMode");
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"));
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"));
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"));
    }

    @Test
    void testUserRoleRepositorySaveCommand() {
        String body;
        UUID id = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();
        String roleId = UUID.randomUUID().toString();
        UserRole userRole =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id);
                            draft.setUserId(userId);
                            draft.setRoleId(roleId);
                        });
        try {
            body = objectMapper.writeValueAsString(userRole);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testUserRoleRepositorySaveCommand");
        Assertions.assertEquals(HttpStatus.SC_OK, response.statusCode());
    }

    @Test
    void testUserRoleRepositorySaveEntities() {
        String body;
        UUID id1 = UUID.randomUUID();
        String userId1 = UUID.randomUUID().toString();
        String roleId1 = UUID.randomUUID().toString();
        UserRole userRole1 =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id1);
                            draft.setUserId(userId1);
                            draft.setRoleId(roleId1);
                        });
        UUID id2 = UUID.randomUUID();
        String userId2 = UUID.randomUUID().toString();
        String roleId2 = UUID.randomUUID().toString();
        UserRole userRole2 =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id2);
                            draft.setUserId(userId2);
                            draft.setRoleId(roleId2);
                        });
        try {
            body = objectMapper.writeValueAsString(List.of(userRole1, userRole2));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testUserRoleRepositorySaveEntities");
        Assertions.assertEquals(HttpStatus.SC_OK, response.statusCode());
        Assertions.assertEquals(id1.toString(), response.jsonPath().getString("[0].id"));
        Assertions.assertEquals(id2.toString(), response.jsonPath().getString("[1].id"));
    }

    @Test
    void testUserRoleRepositorySaveEntitiesSaveMode() {
        String body;
        UUID id1 = UUID.randomUUID();
        String userId1 = UUID.randomUUID().toString();
        String roleId1 = UUID.randomUUID().toString();
        UserRole userRole1 =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id1);
                            draft.setUserId(userId1);
                            draft.setRoleId(roleId1);
                        });
        UUID id2 = UUID.randomUUID();
        String userId2 = UUID.randomUUID().toString();
        String roleId2 = UUID.randomUUID().toString();
        UserRole userRole2 =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id2);
                            draft.setUserId(userId2);
                            draft.setRoleId(roleId2);
                        });
        try {
            body = objectMapper.writeValueAsString(List.of(userRole1, userRole2));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testUserRoleRepositorySaveEntitiesSaveMode");
        Assertions.assertEquals(HttpStatus.SC_OK, response.statusCode());
        Assertions.assertEquals(id1.toString(), response.jsonPath().getString("[0].id"));
        Assertions.assertEquals(userId1, response.jsonPath().getString("[0].userId"));
        Assertions.assertEquals(roleId1, response.jsonPath().getString("[0].roleId"));
        Assertions.assertEquals(id2.toString(), response.jsonPath().getString("[1].id"));
        Assertions.assertEquals(userId2, response.jsonPath().getString("[1].userId"));
        Assertions.assertEquals(roleId2, response.jsonPath().getString("[1].roleId"));
    }

    @Test
    void testUserRoleRepositorySaveEntitiesCommand() {
        String body;
        UUID id1 = UUID.randomUUID();
        String userId1 = UUID.randomUUID().toString();
        String roleId1 = UUID.randomUUID().toString();
        UserRole userRole1 =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id1);
                            draft.setUserId(userId1);
                            draft.setRoleId(roleId1);
                        });
        UUID id2 = UUID.randomUUID();
        String userId2 = UUID.randomUUID().toString();
        String roleId2 = UUID.randomUUID().toString();
        UserRole userRole2 =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id2);
                            draft.setUserId(userId2);
                            draft.setRoleId(roleId2);
                        });
        try {
            body = objectMapper.writeValueAsString(List.of(userRole1, userRole2));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testUserRoleRepositorySaveEntitiesCommand");
        Assertions.assertEquals(HttpStatus.SC_OK, response.statusCode());
    }

    @Test
    void testUserRoleRepositoryUpdate() {
        String body;
        UUID id = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();
        String roleId = UUID.randomUUID().toString();
        UserRole userRole =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id);
                            draft.setUserId(userId);
                            draft.setRoleId(roleId);
                            draft.setDeleteFlag(false);
                        });
        try {
            body = objectMapper.writeValueAsString(userRole);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testUserRoleRepositoryUpdate");
        Assertions.assertEquals(id.toString(), response.jsonPath().getString("id"));
        Assertions.assertEquals(userId, response.jsonPath().getString("userId"));
        Assertions.assertEquals(roleId, response.jsonPath().getString("roleId"));
        Assertions.assertFalse(response.jsonPath().getBoolean("deleteFlag"));
    }

    @Test
    void testUserRoleRepositoryById() {
        Response response =
                requestSpecification
                        .queryParam("id", UUID.fromString("defc2d01-fb38-4d31-b006-fd182b25aa33"))
                        .log()
                        .all()
                        .when()
                        .get("testResources/testUserRoleRepositoryById");
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(
                "defc2d01-fb38-4d31-b006-fd182b25aa33", response.jsonPath().getString("id"));
    }

    @Test
    void testUserRoleRepositoryUpdateInput() {
        String body;
        UUID id = UUID.randomUUID();
        String userId = UUID.randomUUID().toString();
        String roleId = UUID.randomUUID().toString();
        UserRole userRole =
                UserRoleDraft.$.produce(
                        draft -> {
                            draft.setId(id);
                            draft.setUserId(userId);
                            draft.setRoleId(roleId);
                        });
        try {
            body = objectMapper.writeValueAsString(userRole);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .body(body)
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE,
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .log()
                        .all()
                        .when()
                        .put("testResources/testUserRoleRepositoryUpdateInput");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
    }

    @Test
    void testBookRepositoryFindByIdsView() {
        String body;
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(3L);
        ids.add(5L);
        ids.add(7L);
        try {
            body = objectMapper.writeValueAsString(ids);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .body(body)
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindByIdsView");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        Assertions.assertNotNull(response.jsonPath().get("[0].store"));
        Assertions.assertNotNull(response.jsonPath().get("[0].authors"));
    }

    @Test
    void testBookRepositoryFindAllView() {
        Response response =
                requestSpecification
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryFindAllView");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        Assertions.assertNotNull(response.jsonPath().get("[0].store"));
        Assertions.assertNotNull(response.jsonPath().get("[0].authors"));
    }

    @Test
    void testBookRepositoryFindAllTypedPropScalarView() {
        Response response =
                requestSpecification
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryFindAllTypedPropScalarView");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        Assertions.assertEquals(9, response.jsonPath().getLong("[0].id"));
        Assertions.assertNotNull(response.jsonPath().get("[0].store"));
        Assertions.assertNotNull(response.jsonPath().get("[0].authors"));
    }

    @Test
    void testBookRepositoryFindAllSortView() {
        Response response =
                requestSpecification
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryFindAllSortView");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        Assertions.assertEquals(9, response.jsonPath().getLong("[0].id"));
        Assertions.assertNotNull(response.jsonPath().get("[0].store"));
        Assertions.assertNotNull(response.jsonPath().get("[0].authors"));
    }

    @Test
    void testBookRepositoryFindAllPageView() {
        String body;
        Pageable pageable = Pageable.from(0, 1);
        try {
            body = objectMapper.writeValueAsString(pageable);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindAllPageView");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(
                "Learning GraphQL", response.jsonPath().getString("content[0].name"));
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"));
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"));
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"));
    }

    @Test
    void testBookRepositoryFindAllPageTypedPropScalarView() {
        String body;
        Pageable pageable = Pageable.from(0, 1);
        try {
            body = objectMapper.writeValueAsString(pageable);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindAllPageTypedPropScalarView");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(
                "Programming TypeScript", response.jsonPath().getString("content[0].name"));
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"));
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"));
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"));
    }

    @Test
    void testBookRepositoryFindAllPageSortView() {
        String body;
        Pageable pageable = Pageable.from(0, 1);
        try {
            body = objectMapper.writeValueAsString(pageable);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindAllPageSortView");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(
                "Programming TypeScript", response.jsonPath().getString("content[0].name"));
        Assertions.assertNotNull(response.jsonPath().getString("content[0].authors"));
        Assertions.assertEquals(6, response.jsonPath().getInt("totalSize"));
        Assertions.assertNotNull(response.jsonPath().getString("totalPages"));
    }

    @Test
    void testBookRepositoryCustomQuery() {
        Response response =
                requestSpecification
                        .queryParam("id", 1L)
                        .log()
                        .all()
                        .when()
                        .get("testResources/testBookRepositoryCustomQuery");
        Assertions.assertNotNull(response.jsonPath());
    }

    @Test
    void testBookRepositoryFindMapByIdsView() {
        String body;
        try {
            body = objectMapper.writeValueAsString(Arrays.asList(1, 2));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryFindMapByIdsView");
        Assertions.assertNotNull(response.jsonPath().getMap(""));
        Assertions.assertNotNull(response.jsonPath().get("1"));
    }

    @Test
    void testBookRepositoryMerge() {
        String body =
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
                """;
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryMerge");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
    }

    @Test
    void testBookRepositoryMergeInput() {
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
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryMergeInput");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
    }

    @Test
    void testBookRepositoryMergeSaveMode() {
        String body =
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
                """;
        Response response =
                requestSpecification
                        .header(
                                new Header(
                                        HttpHeaders.CONTENT_TYPE.toString(),
                                        HttpHeaderValues.APPLICATION_JSON.toString()))
                        .body(body)
                        .log()
                        .all()
                        .when()
                        .post("testResources/testBookRepositoryMergeSaveMode");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
    }

    @Test
    void testMicronautOrdersSortUtilsStringCodes() {
        Response response =
                requestSpecification
                        .log()
                        .all()
                        .when()
                        .get("testResources/testMicronautOrdersSortUtilsStringCodes");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(11, response.jsonPath().getInt("[0].id"));
    }

    @Test
    void testMicronautOrdersSortUtilsTypedPropScalarProps() {
        Response response =
                requestSpecification
                        .log()
                        .all()
                        .when()
                        .get("testResources/testMicronautOrdersSortUtilsTypedPropScalarProps");
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        Assertions.assertNotNull(response.jsonPath());
        Assertions.assertEquals(1, response.jsonPath().getInt("[0].id"));
    }
}
