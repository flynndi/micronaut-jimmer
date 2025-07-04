package io.micronaut.jimmer.kotlin.it.service.impl

import io.micronaut.jimmer.kotlin.it.entity.BookStore
import io.micronaut.jimmer.kotlin.it.entity.by
import io.micronaut.jimmer.kotlin.it.service.IBookStore
import jakarta.inject.Singleton
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher

@Singleton
class BookStoreImpl(
    private val sqlClient: KSqlClient,
) : IBookStore {
    override fun oneToMany(): List<BookStore> =
        sqlClient
            .createQuery(BookStore::class) {
                select(
                    table.fetch(
                        newFetcher(BookStore::class).by {
                            allScalarFields()
                            books {
                                allScalarFields()
                            }
                        },
                    ),
                )
            }.execute()
}
