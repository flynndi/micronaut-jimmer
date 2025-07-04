package io.micronaut.jimmer.kotlin.it.service.impl

import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.kotlin.it.entity.BookStore
import io.micronaut.jimmer.kotlin.it.entity.by
import io.micronaut.jimmer.kotlin.it.entity.edition
import io.micronaut.jimmer.kotlin.it.entity.id
import io.micronaut.jimmer.kotlin.it.entity.name
import io.micronaut.jimmer.kotlin.it.entity.price
import io.micronaut.jimmer.kotlin.it.entity.store
import io.micronaut.jimmer.kotlin.it.entity.website
import io.micronaut.jimmer.kotlin.it.service.IBook
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asNonNull
import org.babyfish.jimmer.sql.kt.ast.expression.avgAsDecimal
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.babyfish.jimmer.sql.kt.ast.mutation.KSimpleSaveResult
import org.babyfish.jimmer.sql.kt.ast.query.whereIfNotBlank
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import java.math.BigDecimal

@Singleton
open class BookImpl(
    private val sqlClient: KSqlClient,
) : IBook {
    override fun findById(id: Long): Book? = sqlClient.findById(Book::class, id)

    override fun findById(
        id: Int,
        fetcher: Fetcher<Book>,
    ): Book? = sqlClient.findById(fetcher, id)

    override fun findByIds(ids: List<Int>): List<Book> = sqlClient.findByIds(Book::class, ids)

    @Transactional(rollbackFor = [Exception::class])
    override fun save(book: Book): KSimpleSaveResult<Book> {
        val save = sqlClient.save(book)
        val i = 1 / 0
        return save
    }

    override fun findAvgPriceGroupByStoreId(storeIds: Collection<Long>): Map<Long, BigDecimal> =
        sqlClient
            .createQuery(Book::class) {
                where(table.store.id valueIn storeIds)
                groupBy(table.store.id)
                select(
                    table.store.id,
                    avgAsDecimal(table.price).asNonNull(),
                )
            }.execute()
            .associateBy({ it._1 }) {
                it._2
            }

    override fun findBooksByName(
        name: String,
        fetcher: Fetcher<Book>,
    ): List<Book> =
        sqlClient
            .createQuery(Book::class) {
                whereIfNotBlank(name) {
                    table.name.like(name)
                }
                select(table.fetch(fetcher))
            }.execute()

    override fun findBooksByName(name: String): List<Book> =
        sqlClient
            .createQuery(Book::class) {
                whereIfNotBlank(name) {
                    table.name.like(name)
                }
                select(
                    table.fetch(
                        newFetcher(Book::class).by {
                            allScalarFields()
                            store { name }
                        },
                    ),
                )
            }.execute()

    @Transactional(rollbackFor = [Exception::class])
    override fun update() {
        sqlClient
            .createUpdate(BookStore::class) {
                set(table.website, "https://www.manning.com")
                where(table.id eq 2L)
            }.execute()
    }

    override fun manyToMany(): List<Book> =
        sqlClient
            .createQuery(Book::class) {
                where(table.edition eq 1)
                select(
                    table.fetch(
                        newFetcher(Book::class).by {
                            allScalarFields()
                            authors { allScalarFields() }
                            store { allScalarFields() }
                        },
                    ),
                )
            }.execute()

    @Transactional(rollbackFor = [Exception::class])
    override fun updateOneToMany() {
        sqlClient
            .createUpdate(Book::class) {
                set(table.store.id, 2L)
                where(table.id eq 7L)
            }.execute()
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun saveManyToMany() {
        sqlClient.getAssociations(Book::authors).save(10, 3)
    }
}
