package io.micronaut.jimmer.kotlin.it.repository

import io.micronaut.data.model.Pageable
import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.repository.DynamicParam
import io.micronaut.jimmer.repository.KRepository
import io.micronaut.jimmer.repository.annotation.Repository
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.fetcher.Fetcher
import java.math.BigDecimal

@Repository
interface BookRepository : KRepository<Book, Long> {
    fun selectBookById(id: Long): Book? = sql.findById(Book::class, id)

    fun findByNameAndEditionAndPrice(
        name: String,
        edition: Int,
        price: BigDecimal,
        fetcher: Fetcher<Book>,
    ): Book

    fun findByNameLike(
        name: String,
        fetcher: Fetcher<Book>,
    ): List<Book>

    fun findByStoreId(
        storeId: Long,
        fetcher: Fetcher<Book>,
    ): List<Book>

    fun findByNameLikeOrderByName(
        name: String,
        pageable: Pageable,
        fetcher: Fetcher<Book>,
    ): Page<Book>

    fun findByNameLikeIgnoreCaseAndStoreNameOrderByNameAscEditionDesc(
        pageable: Pageable,
        fetcher: Fetcher<Book>,
        @DynamicParam name: String?,
        storeName: String?,
    ): Page<Book>
}
