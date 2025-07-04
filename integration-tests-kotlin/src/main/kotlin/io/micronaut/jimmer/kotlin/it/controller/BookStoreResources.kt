package io.micronaut.jimmer.kotlin.it.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.kotlin.it.entity.BookStore
import io.micronaut.jimmer.kotlin.it.entity.by
import io.micronaut.jimmer.kotlin.it.repository.BookStoreRepository
import io.micronaut.jimmer.kotlin.it.service.IBookStore
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher

@Controller("/bookStoreResource")
class BookStoreResources(
    private val iBookStore: IBookStore,
    private val bookStoreRepository: BookStoreRepository,
) {
    @Get("test1")
    fun test1(): HttpResponse<List<BookStore>> = HttpResponse.ok(iBookStore.oneToMany())

    @Get("testNewestBooks")
    fun testNewestBooks(): HttpResponse<List<BookStore>> =
        HttpResponse.ok(
            bookStoreRepository.findAll(
                newFetcher(BookStore::class).by {
                    name()
                    newestBooks {
                        newFetcher(Book::class).by {
                            allScalarFields()
                            authors {
                                allScalarFields()
                            }
                        }
                    }
                },
            ),
        )
}
