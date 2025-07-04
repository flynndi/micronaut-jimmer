package io.micronaut.jimmer.kotlin.it.service

import io.micronaut.jimmer.kotlin.it.entity.Book
import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.babyfish.jimmer.sql.kt.ast.mutation.KSimpleSaveResult
import java.math.BigDecimal

interface IBook {
    fun findById(id: Long): Book?

    fun findById(
        id: Int,
        fetcher: Fetcher<Book>,
    ): Book?

    fun findByIds(ids: List<Int>): List<Book>?

    fun save(book: Book): KSimpleSaveResult<Book>?

    fun findAvgPriceGroupByStoreId(storeIds: Collection<Long>): Map<Long, BigDecimal>

    fun findBooksByName(
        name: String,
        fetcher: Fetcher<Book>,
    ): List<Book>?

    fun findBooksByName(name: String): List<Book>?

    fun update()

    fun manyToMany(): List<Book>?

    fun updateOneToMany()

    fun saveManyToMany()
}
