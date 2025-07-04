package io.micronaut.jimmer.java.it.resource;

import io.micronaut.context.ApplicationContext;
import io.micronaut.jimmer.java.it.repository.TreeNodeRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class TreeNodeResourcesTestCase {

    @Inject TreeNodeRepository treeNodeRepository;

    @Inject RequestSpecification requestSpecification;

    @Inject ApplicationContext applicationContext;

    @Test
    public void testTreeNodeRepository() {
        TreeNodeRepository treeNodeRepository =
                applicationContext.getBean(TreeNodeRepository.class);
        Assertions.assertEquals(treeNodeRepository, this.treeNodeRepository);
    }

    @Test
    public void testInfiniteRecursion() {
        Response response =
                requestSpecification.log().all().when().get("treeNodeResources/infiniteRecursion");
        Assertions.assertEquals(1, response.jsonPath().getLong("[0].id"));
        Assertions.assertEquals(2, response.jsonPath().getList("[0].childNodes").size());
    }

    @Test
    public void testAll() {
        Response response = requestSpecification.log().all().when().get("treeNodeResources/all");
        Assertions.assertEquals(24, response.jsonPath().getList("").size());
        Assertions.assertEquals(2, response.jsonPath().getList("[0].childNodes").size());
        Assertions.assertEquals(9, response.jsonPath().getInt("[0].childNodes[0].id"));
        Assertions.assertEquals(
                2, response.jsonPath().getList("[0].childNodes[0].childNodes").size());
    }
}
