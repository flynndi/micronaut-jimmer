package io.micronaut.jimmer.kotlin.it.controller

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue
import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.kotlin.it.entity.BookStore
import io.micronaut.jimmer.kotlin.it.entity.UserRole
import io.micronaut.jimmer.kotlin.it.entity.by
import io.micronaut.jimmer.kotlin.it.entity.dto.BookDetailView
import io.micronaut.jimmer.kotlin.it.entity.dto.BookInput
import io.micronaut.jimmer.kotlin.it.entity.dto.UserRoleInput
import io.micronaut.jimmer.kotlin.it.entity.id
import io.micronaut.jimmer.kotlin.it.entity.storeId
import io.micronaut.jimmer.kotlin.it.repository.BookRepository
import io.micronaut.jimmer.kotlin.it.repository.BookStoreRepository
import io.micronaut.jimmer.kotlin.it.repository.UserRoleRepository
import io.micronaut.transaction.annotation.Transactional
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.DeleteMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.mutation.KBatchEntitySaveCommand
import org.babyfish.jimmer.sql.kt.ast.mutation.KSimpleEntitySaveCommand
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import java.util.UUID

@Controller("/testResources")
@Api("test")
open class TestResources(
    private val bookRepository: BookRepository,
    private val bookStoreRepository: BookStoreRepository,
    private val userRoleRepository: UserRoleRepository,
    private val sqlClient: KSqlClient,
) {
    @Get("/test")
    @Api
    fun test(): HttpResponse<List<Book>> {
        val books: List<Book> =
            sqlClient
                .createQuery(Book::class) {
                    select(
                        table.fetch(
                            newFetcher(Book::class).by {
                                allTableFields()
                            },
                        ),
                    )
                }.execute()
        return HttpResponse.ok(books)
    }

    @Get("/testBookRepository")
    @Api
    fun testBookRepository(): HttpResponse<List<Book>> = HttpResponse.ok(bookRepository.findAll())

    @Get("/testBookStoreRepository")
    @Api
    fun testRepository(): HttpResponse<List<BookStore>> = HttpResponse.ok(bookStoreRepository.findAll())

    @Post("/testBookRepositoryPage")
    @Api
    fun testBookRepositoryPage(
        @Body pageable: Pageable,
    ): HttpResponse<Page<Book>> = HttpResponse.ok(bookRepository.findAll(pageable))

    @Post("/testBookRepositoryPageOther")
    @Api
    fun testBookRepositoryPageOther(
        @Body pageable: Pageable,
    ): HttpResponse<Page<Book>> =
        HttpResponse.ok(
            bookRepository.findAll(
                pageable.number,
                pageable.size,
            ),
        )

    @Post("/testBookRepositoryPageSort")
    @Api
    fun testBookRepositoryPageSort(
        @Body pageable: Pageable,
    ): HttpResponse<Page<Book>> =
        HttpResponse.ok(
            bookRepository.findAll(pageable.number, pageable.size, COMPLEX_BOOK, Sort.of(Sort.Order.desc("id"))),
        )

    @Post("/testBookRepositoryPageFetcher")
    @Api
    fun testBookRepositoryPageFetcher(
        @Body pageable: Pageable,
    ): Page<
        @FetchBy("COMPLEX_BOOK")
        Book,
    > = bookRepository.findAll(pageable, COMPLEX_BOOK)

    @Get("/testBookRepositoryById")
    @Api
    fun testBookRepositoryById(
        @QueryValue id: Long,
    ): HttpResponse<Book> = HttpResponse.ok(bookRepository.findNullable(id))

    @Get("/testBookRepositoryByIdOptional")
    @Api
    fun testBookRepositoryByIdOptional(
        @QueryValue id: Long,
    ): HttpResponse<Book> =
        if (bookRepository.findById(id).isPresent) {
            HttpResponse.ok(bookRepository.findById(id).get())
        } else {
            HttpResponse.noContent()
        }

    @Get("/testBookRepositoryByIdFetcher")
    @Api
    fun testBookRepositoryByIdFetcher(
        @QueryValue id: Long,
    ):
        @FetchBy("COMPLEX_BOOK")
        Book? = bookRepository.findNullable(id, COMPLEX_BOOK)

    @Get("/testBookRepositoryByIdFetcherOptional")
    @Api
    fun testBookRepositoryByIdFetcherOptional(
        @QueryValue id: Long,
    ):
        @FetchBy("COMPLEX_BOOK")
        Book? =
        if (bookRepository
                .findById(id, COMPLEX_BOOK)
                .isPresent
        ) {
            bookRepository.findById(id, COMPLEX_BOOK).get()
        } else {
            null
        }

    @Get("/testBookRepositoryViewById")
    @Api
    fun testBookRepositoryViewById(
        @QueryValue id: Long,
    ): HttpResponse<BookDetailView> =
        HttpResponse.ok(
            bookRepository
                .viewer(
                    BookDetailView::class,
                ).findNullable(id),
        )

    @Post("/testBookRepositoryFindAllById")
    @Api
    fun testBookRepositoryFindAllById(
        @Body ids: List<Long>,
    ): HttpResponse<List<Book>> = HttpResponse.ok(bookRepository.findAllById(ids))

    @Post("/testBookRepositoryFindByIdsFetcher")
    @Api
    fun testBookRepositoryFindByIdsFetcher(
        @Body ids: List<Long>,
    ): HttpResponse<List<Book>> =
        HttpResponse.ok(
            bookRepository.findByIds(
                ids,
                COMPLEX_BOOK,
            ),
        )

    @Post("/testBookRepositoryFindMapByIds")
    @Api
    fun testBookRepositoryFindMapByIds(
        @Body ids: List<Long>,
    ): HttpResponse<Map<Long, Book>> =
        HttpResponse.ok(
            bookRepository.findMapByIds(
                ids,
            ),
        )

    @Post("/testBookRepositoryFindMapByIdsFetcher")
    @Api
    fun testBookRepositoryFindMapByIdsFetcher(
        @Body ids: List<Long>,
    ): HttpResponse<Map<Long, Book>> =
        HttpResponse.ok(
            bookRepository.findMapByIds(
                ids,
                COMPLEX_BOOK,
            ),
        )

    @Get("/testBookRepositoryFindAll")
    @Api
    fun testBookRepositoryFindAll(): HttpResponse<List<Book>> = HttpResponse.ok(bookRepository.findAll())

    @Get("/testBookRepositoryFindAllTypedPropScalar")
    @Api
    fun testBookRepositoryFindAllTypedPropScalar(): HttpResponse<List<Book>> =
        HttpResponse.ok(bookRepository.findAll(Sort.of(Sort.Order.desc("name"))))

    @Get("/testBookRepositoryFindAllFetcherTypedPropScalar")
    @Api
    fun testBookRepositoryFindAllFetcherTypedPropScalar(): HttpResponse<List<Book>> =
        HttpResponse.ok<List<Book>>(
            bookRepository.findAll(
                COMPLEX_BOOK,
                Sort.of(Sort.Order.desc("name")),
            ),
        )

    @Get("/testBookRepositoryFindAllSort")
    @Api
    fun testBookRepositoryFindAllSort(): HttpResponse<List<Book>> =
        HttpResponse.ok(
            bookRepository.findAll(
                Sort.of(
                    Sort.Order.desc("name"),
                ),
            ),
        )

    @Get("/testBookRepositoryFindAllFetcherSort")
    @Api
    fun testBookRepositoryFindAllFetcherSort(): HttpResponse<List<Book>> =
        HttpResponse.ok(
            bookRepository.findAll(
                COMPLEX_BOOK,
                Sort.of(Sort.Order.desc("name")),
            ),
        )

    @Post("/testBookRepositoryFindAllPageFetcher")
    @Api
    fun testBookRepositoryFindAllPageFetcher(
        @Body pageable: Pageable,
    ): HttpResponse<Page<Book>> =
        HttpResponse.ok(
            bookRepository.findAll(
                pageable.number,
                pageable.size,
                COMPLEX_BOOK,
            ),
        )

    @Post("/testBookRepositoryFindAllPageTypedPropScalar")
    @Api
    fun testBookRepositoryFindAllPageTypedPropScalar(
        @Body pageable: Pageable,
    ): HttpResponse<Page<Book>> =
        HttpResponse.ok(
            bookRepository.findAll(
                pageable.number,
                pageable.size,
                newFetcher(Book::class).by { allTableFields() },
                Sort.of(Sort.Order.desc("name")),
            ),
        )

    @Post("/testBookRepositoryFindAllPageFetcherTypedPropScalar")
    @Api
    fun testBookRepositoryFindAllPageFetcherTypedPropScalar(
        @Body pageable: Pageable,
    ): HttpResponse<Page<Book>> =
        HttpResponse.ok(
            bookRepository.findAll(
                pageable.number,
                pageable.size,
                newFetcher(Book::class).by {
                    allScalarFields()
                    store { name() }
                    authors {
                        firstName()
                        lastName()
                    }
                },
                Sort.of(Sort.Order.desc("name")),
            ),
        )

    @Post("/testBookRepositoryFindAllPageSort")
    @Api
    fun testBookRepositoryFindAllPageSort(
        @Body pageable: Pageable,
    ): HttpResponse<Page<Book>> =
        HttpResponse.ok(
            bookRepository.findAll(
                pageable.number,
                pageable.size,
                newFetcher(Book::class).by { allTableFields() },
                Sort.of(Sort.Order.desc("name")),
            ),
        )

    @Post("/testBookRepositoryFindAllPageFetcherSort")
    @Api
    fun testBookRepositoryFindAllPageFetcherSort(
        @Body pageable: Pageable,
    ): HttpResponse<Page<Book>> =
        HttpResponse.ok(
            bookRepository.findAll(
                pageable.number,
                pageable.size,
                newFetcher(Book::class).by {
                    allScalarFields()
                    store { name() }
                    authors {
                        firstName()
                        lastName()
                    }
                },
                Sort.of(Sort.Order.desc("name")),
            ),
        )

    @Get("/testBookRepositoryExistsById")
    @Api
    fun testBookRepositoryExistsById(
        @QueryValue id: Long,
    ): HttpResponse<Boolean> = HttpResponse.ok(bookRepository.existsById(id))

    @Get("/testBookRepositoryCount")
    @Api
    fun testBookRepositoryCount(): HttpResponse<Long> = HttpResponse.ok<Long>(bookRepository.count())

    @Post("/testUserRoleRepositoryInsert")
    @Api
    @Transactional
    open fun testUserRoleRepositoryInsert(
        @Body userRole: UserRole,
    ): HttpResponse<UserRole> = HttpResponse.ok(userRoleRepository.insert(userRole))

    @Post("/testUserRoleRepositoryInsertInput")
    @Transactional(rollbackFor = [Exception::class])
    @Api
    open fun testUserRoleRepositoryInsertInput(
        @Body userRoleInput: UserRoleInput,
    ): HttpResponse<UserRole> = HttpResponse.ok(userRoleRepository.insert(userRoleInput))

    @Post("/testUserRoleRepositorySave")
    @Transactional(rollbackFor = [Exception::class])
    @Api
    open fun testUserRoleRepositorySave(
        @Body userRole: UserRole,
    ): HttpResponse<UserRole> =
        HttpResponse.ok(
            userRoleRepository.save(
                userRole,
            ),
        )

    @Post("/testUserRoleRepositorySaveInput")
    @Transactional(rollbackFor = [Exception::class])
    @Api
    open fun testUserRoleRepositorySaveInput(
        @Body userRoleInput: UserRoleInput,
    ): HttpResponse<UserRole> = HttpResponse.ok(userRoleRepository.save(userRoleInput))

    @Post("/testUserRoleRepositorySaveInputSaveMode")
    @Transactional(rollbackFor = [Exception::class])
    @Api
    open fun testUserRoleRepositorySaveInputSaveMode(
        @Body userRoleInput: UserRoleInput,
    ): HttpResponse<UserRole> =
        HttpResponse.ok(
            userRoleRepository.save(
                userRoleInput,
                SaveMode.INSERT_ONLY,
            ),
        )

    @Post("/testUserRoleRepositorySaveCommand")
    @Transactional(rollbackFor = [java.lang.Exception::class])
    @Api
    open fun testUserRoleRepositorySaveCommand(
        @Body userRoleInput: UserRoleInput,
    ): HttpResponse<KSimpleEntitySaveCommand<UserRole>> =
        HttpResponse.ok(
            userRoleRepository.saveCommand(userRoleInput),
        )

    @Post("/testUserRoleRepositorySaveEntities")
    @Transactional(rollbackFor = [java.lang.Exception::class])
    @Api
    open fun testUserRoleRepositorySaveEntities(
        @Body list: List<UserRole>,
    ): HttpResponse<List<UserRole>> =
        HttpResponse.ok(
            userRoleRepository.saveEntities(
                list,
            ),
        )

    @Post("/testUserRoleRepositorySaveEntitiesSaveMode")
    @Transactional(rollbackFor = [Exception::class])
    @Api
    open fun testUserRoleRepositorySaveEntitiesSaveMode(
        @Body list: List<UserRole>,
    ): HttpResponse<List<UserRole>> =
        HttpResponse.ok(
            userRoleRepository.saveEntities(
                list,
                SaveMode.INSERT_ONLY,
            ),
        )

    @Post("/testUserRoleRepositorySaveEntitiesCommand")
    @Transactional(rollbackFor = [Exception::class])
    @Api
    open fun testUserRoleRepositorySaveEntitiesCommand(
        @Body list: List<UserRole>,
    ): HttpResponse<KBatchEntitySaveCommand<UserRole>> =
        HttpResponse.ok(
            userRoleRepository.saveEntitiesCommand(
                list,
            ),
        )

    @Delete("/testUserRoleRepositoryDeleteAll")
    @Transactional(rollbackFor = [Exception::class])
    @Api
    open fun testUserRoleRepositoryDeleteAll(list: List<UserRole>): HttpResponse<Int> =
        HttpResponse.ok(userRoleRepository.deleteAll(list, DeleteMode.AUTO))

    @Post("/testUserRoleRepositoryUpdate")
    @Transactional(rollbackFor = [Exception::class])
    @Api
    open fun testUserRoleRepositoryUpdate(
        @Body userRole: UserRole,
    ): HttpResponse<UserRole> =
        HttpResponse.ok(
            userRoleRepository.update(
                userRole,
            ),
        )

    @Get("/testUserRoleRepositoryById")
    @Api
    fun testUserRoleRepositoryById(
        @QueryValue id: UUID,
    ): HttpResponse<UserRole> = HttpResponse.ok(userRoleRepository.findNullable(id))

    @Put("/testUserRoleRepositoryUpdateInput")
    @Transactional(rollbackFor = [java.lang.Exception::class])
    @Api
    open fun testUserRoleRepositoryUpdateInput(
        @Body userRoleInput: UserRoleInput,
    ): HttpResponse<Void> {
        userRoleRepository.update(userRoleInput)
        return HttpResponse.ok()
    }

    @Post("/testBookRepositoryFindByIdsView")
    @Api
    fun testBookRepositoryFindByIdsView(
        @Body ids: List<Long>,
    ): HttpResponse<List<BookDetailView>> =
        HttpResponse.ok(
            bookRepository
                .viewer(
                    BookDetailView::class,
                ).findByIds(ids),
        )

    @Get("/testBookRepositoryFindAllView")
    @Api
    fun testBookRepositoryFindAllView(): HttpResponse<List<BookDetailView>> =
        HttpResponse.ok(
            bookRepository
                .viewer(
                    BookDetailView::class,
                ).findAll(),
        )

    @Get("/testBookRepositoryFindAllTypedPropScalarView")
    @Api
    fun testBookRepositoryFindAllTypedPropScalarView(): HttpResponse<List<BookDetailView>> =
        HttpResponse.ok(
            bookRepository
                .viewer(BookDetailView::class)
                .findAll(Sort.of(Sort.Order.desc("name"))),
        )

    @Get("/testBookRepositoryFindAllSortView")
    @Api
    fun testBookRepositoryFindAllSortView(): HttpResponse<List<BookDetailView>> =
        HttpResponse.ok(
            bookRepository
                .viewer(BookDetailView::class)
                .findAll(Sort.of(Sort.Order.desc("name"))),
        )

    @Post("/testBookRepositoryFindAllPageView")
    @Api
    fun testBookRepositoryFindAllPageView(
        @Body pageable: Pageable,
    ): HttpResponse<Page<BookDetailView>> =
        HttpResponse.ok(
            bookRepository
                .viewer(BookDetailView::class)
                .findAll(pageable.number, pageable.size),
        )

    @Post("/testBookRepositoryFindAllPageTypedPropScalarView")
    @Api
    fun testBookRepositoryFindAllPageTypedPropScalarView(
        @Body pageable: Pageable,
    ): HttpResponse<Page<BookDetailView>> =
        HttpResponse.ok(
            bookRepository
                .viewer(BookDetailView::class)
                .findAll(
                    pageable.getNumber(),
                    pageable.getSize(),
                    Sort.of(Sort.Order.desc("name")),
                ),
        )

    @Post("/testBookRepositoryFindAllPageSortView")
    @Api
    fun testBookRepositoryFindAllPageSortView(
        @Body pageable: Pageable,
    ): HttpResponse<Page<BookDetailView>> =
        HttpResponse.ok(
            bookRepository
                .viewer(BookDetailView::class)
                .findAll(
                    pageable.getNumber(),
                    pageable.getSize(),
                    Sort.of(Sort.Order.desc("name")),
                ),
        )

    @Get("/testBookRepositoryCustomQuery")
    @Api
    fun testBookRepositoryCustomQuery(
        @QueryValue id: Long,
    ): HttpResponse<Book> = HttpResponse.ok(bookRepository.selectBookById(id))

    @Post("/testBookRepositoryFindMapByIdsView")
    @Api
    fun testBookRepositoryFindMapByIdsView(
        @Body ids: List<Long>,
    ): HttpResponse<Map<Long, BookDetailView>> =
        HttpResponse.ok(
            bookRepository
                .viewer(BookDetailView::class)
                .findMapByIds(ids),
        )

    @Post("/testBookRepositoryMerge")
    @Api
    @Transactional
    open fun testBookRepositoryMerge(
        @Body book: Book,
    ): HttpResponse<Book> =
        HttpResponse.ok(
            bookRepository.save(
                book,
                SaveMode.UPSERT,
                AssociatedSaveMode.MERGE,
            ),
        )

    @Post("/testBookRepositoryMergeInput")
    @Api
    @Transactional
    open fun testBookRepositoryMergeInput(
        @Body bookInput: BookInput,
    ): HttpResponse<Book> =
        HttpResponse.ok(
            bookRepository.save(bookInput),
        )

    @Post("/testBookRepositoryMergeSaveMode")
    @Api
    @Transactional
    open fun testBookRepositoryMergeSaveMode(
        @Body book: Book,
    ): HttpResponse<Book> = HttpResponse.ok(bookRepository.save(book, SaveMode.UPSERT, AssociatedSaveMode.REPLACE))

    @Post("/testEvent")
    @Transactional
    @Api
    open fun testEvent(): HttpResponse<Void> {
        sqlClient
            .createUpdate(Book::class) {
                set(table.storeId, 2L)
                where(table.id eq 7L)
            }.execute()
        return HttpResponse.ok()
    }

    companion object {
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
