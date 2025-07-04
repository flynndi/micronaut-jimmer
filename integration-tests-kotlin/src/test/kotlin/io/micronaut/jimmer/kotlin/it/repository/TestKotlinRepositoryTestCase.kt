package io.micronaut.jimmer.kotlin.it.repository

import io.micronaut.context.ApplicationContext
import io.micronaut.jimmer.kotlin.it.Constant
import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.kotlin.it.entity.UserRole
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

@MicronautTest
class TestKotlinRepositoryTestCase {
    @Inject
    lateinit var bookKotlinRepository: BookKotlinRepository

    @Inject
    lateinit var userRoleKotlinRepository: UserRoleKotlinRepository

    @Inject
    lateinit var applicationContext: ApplicationContext

    @Test
    fun testJavaRepositoryBean() {
        val bookKotlinRepositoryFromArc: BookKotlinRepository =
            applicationContext.getBean(BookKotlinRepository::class.java)
        val userRoleKotlinRepositoryFromArc: UserRoleKotlinRepository =
            applicationContext.getBean(UserRoleKotlinRepository::class.java)
        Assertions.assertEquals(bookKotlinRepository, bookKotlinRepositoryFromArc)
        Assertions.assertEquals(userRoleKotlinRepository, userRoleKotlinRepositoryFromArc)
    }

    @Test
    fun testBookJavaRepositoryFindById() {
        val book: Book? = bookKotlinRepository.findById(1L)
        Assertions.assertNotNull(book)
        Assertions.assertEquals(1L, book!!.id)
    }

    @Test
    fun testUserRoleJavaRepositoryFindById() {
        val userRole: UserRole? =
            userRoleKotlinRepository.findById(UUID.fromString(Constant.USER_ROLE_ID))
        Assertions.assertNotNull(userRole)
        Assertions.assertEquals(UUID.fromString(Constant.USER_ROLE_ID), userRole!!.id)
    }

    @Test
    fun testMethodInBookJavaRepositoryFindById() {
        val book: Book? = bookKotlinRepository.methodInBookJavaRepositoryFindById(1L)
        Assertions.assertNotNull(book)
        Assertions.assertEquals(1L, book!!.id)
    }
}
