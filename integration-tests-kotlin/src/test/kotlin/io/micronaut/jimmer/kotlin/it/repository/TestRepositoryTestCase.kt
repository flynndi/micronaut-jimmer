package io.micronaut.jimmer.kotlin.it.repository

import io.micronaut.context.ApplicationContext
import io.micronaut.data.model.Pageable
import io.micronaut.jimmer.kotlin.it.Constant
import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.kotlin.it.entity.UserRole
import io.micronaut.jimmer.kotlin.it.entity.by
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.math.BigDecimal

@MicronautTest
class TestRepositoryTestCase {
    @Inject
    lateinit var bookRepository: BookRepository

    @Inject
    lateinit var userRoleRepository: UserRoleRepository

    @Inject
    lateinit var applicationContext: ApplicationContext

    @Test
    fun testRepositoryBean() {
        val bookRepositoryFromArc: BookRepository? =
            applicationContext.getBean(BookRepository::class.java)
        Assertions.assertEquals(bookRepository, bookRepositoryFromArc)
        val userRoleRepositoryFromArc: UserRoleRepository? =
            applicationContext.getBean(UserRoleRepository::class.java)
        Assertions.assertEquals(userRoleRepository, userRoleRepositoryFromArc)
    }

    @Test
    fun testBookRepositoryFindByNameAndEditionAndPrice() {
        val book: Book =
            bookRepository.findByNameAndEditionAndPrice(
                "Learning GraphQL",
                1,
                BigDecimal(50),
                newFetcher(Book::class).by {
                    allTableFields()
                },
            )
        Assertions.assertEquals("Learning GraphQL", book.name)
        Assertions.assertEquals(1, book.edition)
        Assertions.assertEquals(BigDecimal("50.00"), book.price)
    }

    @Test
    fun testBookRepositoryFindByNameLike() {
        val books: List<Book> =
            bookRepository.findByNameLike(
                "Learning GraphQL",
                newFetcher(Book::class).by { allTableFields() },
            )
        Assertions.assertEquals(2, books.size)
        Assertions.assertEquals("Learning GraphQL", books.get(0).name)
        Assertions.assertEquals("Learning GraphQL", books.get(1).name)
    }

    @Test
    fun testBookRepositoryFindByStoreId() {
        val books: List<Book> =
            bookRepository.findByStoreId(1L, newFetcher(Book::class).by { allTableFields() })
        Assertions.assertEquals(5, books.size)
        Assertions.assertEquals(1L, books[0].storeId)
        Assertions.assertEquals(1L, books[1].storeId)
        Assertions.assertEquals(1L, books[2].storeId)
        Assertions.assertEquals(1L, books[3].storeId)
        Assertions.assertEquals(1L, books[4].storeId)
    }

    @Test
    fun testBookRepositoryFindByNameLikeOrderByName() {
        val bookPage: Page<Book> =
            bookRepository.findByNameLikeOrderByName(
                "Learning GraphQL",
                Pageable.from(0, 10),
                newFetcher(Book::class).by { allTableFields() },
            )
        Assertions.assertEquals(2, bookPage.getRows().size)
        Assertions.assertEquals("Learning GraphQL", bookPage.getRows()[0].name)
        Assertions.assertEquals("Learning GraphQL", bookPage.getRows()[1].name)
        Assertions.assertEquals(1, bookPage.totalPageCount)
        Assertions.assertEquals(2, bookPage.totalRowCount)
    }

    @Test
    fun testBookRepositoryFindByNameLikeIgnoreCaseAndStoreNameOrderByNameAscEditionDesc() {
        val bookPage: Page<Book> =
            bookRepository.findByNameLikeIgnoreCaseAndStoreNameOrderByNameAscEditionDesc(
                Pageable.from(0, 10),
                newFetcher(Book::class).by { allTableFields() },
                null,
                "MANNING",
            )
        Assertions.assertEquals(1, bookPage.totalPageCount)
        Assertions.assertEquals(1, bookPage.totalRowCount)
    }

    @Test
    fun testBookRepositoryFindByNameLikeIgnoreCaseAndStoreNameOrderByNameAscEditionDescWithNPE() {
        Assertions.assertThrows(
            NullPointerException::class.java,
            Executable {
                bookRepository
                    .findByNameLikeIgnoreCaseAndStoreNameOrderByNameAscEditionDesc(
                        Pageable.from(0, 10),
                        newFetcher(Book::class).by { allTableFields() },
                        null,
                        null,
                    )
            },
        )
    }

    @Test
    fun testUserRoleRepositoryFindByUserId() {
        val userRole: UserRole =
            userRoleRepository.findByUserId(Constant.USER_ID)
        Assertions.assertEquals(Constant.USER_ID, userRole.userId)
    }

    @Test
    fun testUserRoleRepositoryFindByRoleId() {
        val userRole: UserRole =
            userRoleRepository.findByRoleId(Constant.ROLE_ID)
        Assertions.assertEquals(Constant.ROLE_ID, userRole.roleId)
    }

    @Test
    fun testUserRoleRepositoryFindByUserIdAndRoleId() {
        val userRole: UserRole =
            userRoleRepository.findByUserIdAndRoleId(
                Constant.USER_ID,
                Constant.ROLE_ID,
            )
        Assertions.assertEquals(Constant.USER_ID, userRole.userId)
        Assertions.assertEquals(Constant.ROLE_ID, userRole.roleId)
    }
}
