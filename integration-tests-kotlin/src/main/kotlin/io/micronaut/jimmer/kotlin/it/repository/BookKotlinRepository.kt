package io.micronaut.jimmer.kotlin.it.repository

import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.repo.support.AbstractKotlinRepository
import jakarta.inject.Singleton
import org.babyfish.jimmer.sql.kt.KSqlClient

@Singleton
class BookKotlinRepository(
    kSqlClient: KSqlClient,
) : AbstractKotlinRepository<Book, Long>(kSqlClient) {
    fun methodInBookJavaRepositoryFindById(id: Long): Book? = sql.findById(Book::class, id)
}
