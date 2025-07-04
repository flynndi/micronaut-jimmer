package io.micronaut.jimmer.java.it.repository;

import io.micronaut.context.ApplicationContext;
import io.micronaut.jimmer.java.it.Constant;
import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.java.it.entity.UserRole;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class TestJavaRepositoryTestCase {

    @Inject BookJavaRepository bookJavaRepository;

    @Inject UserRoleJavaRepository userRoleJavaRepository;

    @Inject ApplicationContext applicationContext;

    @Test
    void testJavaRepositoryBean() {
        BookJavaRepository bookJavaRepositoryFromArc =
                applicationContext.getBean(BookJavaRepository.class);
        UserRoleJavaRepository userRoleJavaRepositoryFromArc =
                applicationContext.getBean(UserRoleJavaRepository.class);
        Assertions.assertEquals(bookJavaRepository, bookJavaRepositoryFromArc);
        Assertions.assertEquals(userRoleJavaRepository, userRoleJavaRepositoryFromArc);
    }

    @Test
    void testBookJavaRepositoryFindById() {
        Book book = bookJavaRepository.findById(1L);
        Assertions.assertNotNull(book);
        Assertions.assertEquals(1L, book.id());
    }

    @Test
    void testUserRoleJavaRepositoryFindById() {
        UserRole userRole = userRoleJavaRepository.findById(UUID.fromString(Constant.USER_ROLE_ID));
        Assertions.assertNotNull(userRole);
        Assertions.assertEquals(UUID.fromString(Constant.USER_ROLE_ID), userRole.id());
    }

    @Test
    void testMethodInBookJavaRepositoryFindById() {
        Book book = bookJavaRepository.methodInBookJavaRepositoryFindById(1L);
        Assertions.assertNotNull(book);
        Assertions.assertEquals(1L, book.id());
    }
}
