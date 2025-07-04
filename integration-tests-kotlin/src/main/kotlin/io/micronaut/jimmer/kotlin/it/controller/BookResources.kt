package io.micronaut.jimmer.kotlin.it.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.annotation.RequestBean
import io.micronaut.http.multipart.FileUpload
import io.micronaut.jimmer.kotlin.it.config.error.UserInfoException
import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.kotlin.it.entity.by
import io.micronaut.jimmer.kotlin.it.entity.dto.BookDetailView
import io.micronaut.jimmer.kotlin.it.entity.dto.BookSpecification
import io.micronaut.jimmer.kotlin.it.service.IBook
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.mutation.KSimpleSaveResult
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher

@Controller("/bookResource")
@Api
class BookResources(
    private val sqlClient: KSqlClient,
    private val iBook: IBook,
) {
    @Get("/book")
    @Api
    fun getBookById(
        @QueryValue id: Long,
    ): HttpResponse<Book?>? = HttpResponse.ok(iBook.findById(id))

    @Get("/book/{id}")
    @Api
    fun getBookByIdFetcher(
        @PathVariable id: Int,
    ):
        @FetchBy("COMPLEX_BOOK")
        Book? = iBook.findById(id, COMPLEX_BOOK)

    @Post("/books")
    @Api
    fun getBookByIds(ids: List<Int>): HttpResponse<List<Book>> = HttpResponse.ok(iBook.findByIds(ids))

    @Post("/book")
    @Api
    fun postBook(book: Book): HttpResponse<KSimpleSaveResult<Book>> = HttpResponse.ok(iBook.save(book))

    @Get("/books")
    @Api
    fun getBookByNameFetcher(
        @QueryValue name: String,
    ): List<Book>? = iBook.findBooksByName(name, SIMPLE_BOOK)

    @Get("/booksByName")
    @Api
    fun getBookByName(
        @QueryValue name: String,
    ): List<Book>? = iBook.findBooksByName(name)

    @Put("/update")
    @Api
    fun update(): HttpResponse<Void> {
        iBook.update()
        return HttpResponse.ok()
    }

    @Get("/testManyToMany")
    @Api
    fun testManyToMany(): HttpResponse<List<Book>> = HttpResponse.ok(iBook.manyToMany())

    @Put("/testUpdateOneToMany")
    @Api
    fun testUpdateOneToMany(): HttpResponse<Void> {
        iBook.updateOneToMany()
        return HttpResponse.ok()
    }

    @Post("/testSaveManyToMany")
    @Api
    fun testSaveManyToMany(): HttpResponse<Void> {
        iBook.saveManyToMany()
        return HttpResponse.ok()
    }

    @Post("/testFile")
    @Api
    fun testFile(filePart: FileUpload?): HttpResponse<Void> = HttpResponse.ok()

    @Get("/testError")
    @Api
    @Throws(UserInfoException.IllegalUserName::class)
    fun testError(): HttpResponse<Void> {
        val illegalChars: MutableList<Character> = ArrayList()
        throw UserInfoException.illegalUserName(
            message = "testError",
            illegalChars = illegalChars,
        )
    }

    @Get("/testBookSpecification")
    @Api
    fun testBoolSpecification(
        @RequestBean bookSpecification: BookSpecification,
    ): HttpResponse<List<BookDetailView>> =
        HttpResponse.ok(
            sqlClient
                .createQuery(Book::class) {
                    where(bookSpecification)
                    select(table.fetch(BookDetailView::class))
                }.execute(),
        )

    companion object {
        val SIMPLE_BOOK: Fetcher<Book> =
            newFetcher(Book::class).by {
                name()
            }

        val COMPLEX_BOOK: Fetcher<Book> =
            newFetcher(Book::class).by {
                allScalarFields()
                store { name() }
                authors {
                    firstName()
                    lastName()
                }
            }
    }
}
