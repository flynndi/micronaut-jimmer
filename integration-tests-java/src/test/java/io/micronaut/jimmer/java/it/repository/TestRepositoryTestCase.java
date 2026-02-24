package io.micronaut.jimmer.java.it.repository;

import io.micronaut.context.ApplicationContext;
import io.micronaut.data.model.Pageable;
import io.micronaut.jimmer.java.it.Constant;
import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.java.it.entity.Fetchers;
import io.micronaut.jimmer.java.it.entity.UserRole;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import org.babyfish.jimmer.Page;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class TestRepositoryTestCase {

    @Inject BookRepository bookRepository;

    @Inject UserRoleRepository userRoleRepository;

    @Inject ApplicationContext applicationContext;

    @Test
    void testRepositoryBean() {
        BookRepository bookRepositoryFromArc = applicationContext.getBean(BookRepository.class);
        Assertions.assertEquals(bookRepository, bookRepositoryFromArc);
        UserRoleRepository userRoleRepositoryFromArc =
                applicationContext.getBean(UserRoleRepository.class);
        Assertions.assertEquals(userRoleRepository, userRoleRepositoryFromArc);
    }

    @Test
    void testBookRepositoryFindByNameAndEditionAndPrice() {
        Book book =
                bookRepository.findByNameAndEditionAndPrice(
                        "Learning GraphQL",
                        1,
                        new BigDecimal(50),
                        Fetchers.BOOK_FETCHER.allTableFields());
        Assertions.assertEquals("Learning GraphQL", book.name());
        Assertions.assertEquals(1, book.edition());
        Assertions.assertEquals(new BigDecimal("50.00"), book.price());
    }

    @Test
    void testBookRepositoryFindByNameLike() {
        List<Book> books =
                bookRepository.findByNameLike(
                        "Learning GraphQL", Fetchers.BOOK_FETCHER.allTableFields());
        Assertions.assertEquals(2, books.size());
        Assertions.assertEquals("Learning GraphQL", books.get(0).name());
        Assertions.assertEquals("Learning GraphQL", books.get(1).name());
    }

    @Test
    void testBookRepositoryFindByStoreId() {
        List<Book> books = bookRepository.findByStoreId(1L, Fetchers.BOOK_FETCHER.allTableFields());
        Assertions.assertEquals(5, books.size());
        Assertions.assertEquals(1L, books.get(0).storeId());
        Assertions.assertEquals(1L, books.get(1).storeId());
        Assertions.assertEquals(1L, books.get(2).storeId());
        Assertions.assertEquals(1L, books.get(3).storeId());
        Assertions.assertEquals(1L, books.get(4).storeId());
    }

    @Test
    void testBookRepositoryFindByNameLikeOrderByName() {
        Page<Book> bookPage =
                bookRepository.findByNameLikeOrderByName(
                        "Learning GraphQL",
                        Pageable.from(0, 10),
                        Fetchers.BOOK_FETCHER.allTableFields());
        Assertions.assertEquals(2, bookPage.getRows().size());
        Assertions.assertEquals("Learning GraphQL", bookPage.getRows().get(0).name());
        Assertions.assertEquals("Learning GraphQL", bookPage.getRows().get(1).name());
        Assertions.assertEquals(1, bookPage.getTotalPageCount());
        Assertions.assertEquals(2, bookPage.getTotalRowCount());
    }

    @Test
    void testBookRepositoryFindByNameLikeIgnoreCaseAndStoreNameOrderByNameAscEditionDesc() {
        Page<Book> bookPage =
                bookRepository.findByNameLikeIgnoreCaseAndStoreNameOrderByNameAscEditionDesc(
                        Pageable.from(0, 10),
                        Fetchers.BOOK_FETCHER.allTableFields(),
                        null,
                        "MANNING");
        Assertions.assertEquals(1, bookPage.getTotalPageCount());
        Assertions.assertEquals(1, bookPage.getTotalRowCount());
    }

    @Test
    void testBookRepositoryFindByNameLikeIgnoreCaseAndStoreNameOrderByNameAscEditionDescWithNPE() {
        Assertions.assertThrows(
                NullPointerException.class,
                () ->
                        bookRepository
                                .findByNameLikeIgnoreCaseAndStoreNameOrderByNameAscEditionDesc(
                                        Pageable.from(0, 10),
                                        Fetchers.BOOK_FETCHER.allTableFields(),
                                        null,
                                        null));
    }

    @Test
    void testUserRoleRepositoryFindByUserId() {
        UserRole userRole = userRoleRepository.findByUserId(Constant.USER_ID);
        Assertions.assertEquals(Constant.USER_ID, userRole.userId());
    }

    @Test
    void testUserRoleRepositoryFindByRoleId() {
        UserRole userRole = userRoleRepository.findByRoleId(Constant.ROLE_ID);
        Assertions.assertEquals(Constant.ROLE_ID, userRole.roleId());
    }

    @Test
    void testUserRoleRepositoryFindByUserIdAndRoleId() {
        UserRole userRole =
                userRoleRepository.findByUserIdAndRoleId(Constant.USER_ID, Constant.ROLE_ID);
        Assertions.assertEquals(Constant.USER_ID, userRole.userId());
    }
}
