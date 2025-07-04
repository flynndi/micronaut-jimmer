package io.micronaut.jimmer.java.it.controller;

import static io.micronaut.jimmer.java.it.entity.Fetchers.*;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.jimmer.java.it.entity.*;
import io.micronaut.jimmer.java.it.entity.dto.BookDetailView;
import io.micronaut.jimmer.java.it.entity.dto.BookInput;
import io.micronaut.jimmer.java.it.entity.dto.UserRoleInput;
import io.micronaut.jimmer.java.it.repository.BookRepository;
import io.micronaut.jimmer.java.it.repository.BookStoreRepository;
import io.micronaut.jimmer.java.it.repository.UserRoleRepository;
import io.micronaut.jimmer.model.SortUtils;
import io.micronaut.jimmer.repository.MicronautOrders;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.Api;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.*;
import org.babyfish.jimmer.sql.fetcher.Fetcher;

@Controller("/testResources")
@Api("test")
public class TestResources {

    @Inject BookRepository bookRepository;

    @Inject BookStoreRepository bookStoreRepository;

    @Inject UserRoleRepository userRoleRepository;

    @Inject JSqlClient sqlClient;

    @Get("/test")
    @Api
    public HttpResponse<List<Book>> test() {
        List<Book> books =
                sqlClient
                        .createQuery(Tables.BOOK_TABLE)
                        .select(Tables.BOOK_TABLE.fetch(BookFetcher.$.allTableFields()))
                        .execute();
        return HttpResponse.ok(books);
    }

    @Get("/testBookRepository")
    @Api
    public HttpResponse<List<Book>> testBookRepository() {
        return HttpResponse.ok(bookRepository.findAll());
    }

    @Get("/testBookStoreRepository")
    @Api
    public HttpResponse<List<BookStore>> testRepository() {
        return HttpResponse.ok(bookStoreRepository.findAll());
    }

    @Post("/testBookRepositoryPage")
    @Api
    public HttpResponse<Page<Book>> testBookRepositoryPage(@Body Pageable pageable) {
        return HttpResponse.ok(bookRepository.findAll(pageable));
    }

    @Post("/testBookRepositoryPageOther")
    @Api
    public HttpResponse<Page<Book>> testBookRepositoryPageOther(@Body Pageable pageable) {
        return HttpResponse.ok(bookRepository.findAll(pageable.getNumber(), pageable.getSize()));
    }

    @Post("/testBookRepositoryPageSort")
    @Api
    public HttpResponse<Page<Book>> testBookRepositoryPageSort(@Body Pageable pageable) {
        return HttpResponse.ok(
                bookRepository.findAll(
                        pageable.getNumber(), pageable.getSize(), Sort.of(Sort.Order.desc("id"))));
    }

    @Post("/testBookRepositoryPageFetcher")
    @Api
    public Page<@FetchBy("COMPLEX_BOOK") Book> testBookRepositoryPageFetcher(
            @Body Pageable pageable) {
        return bookRepository.findAll(pageable, COMPLEX_BOOK);
    }

    @Get("/testBookRepositoryById")
    @Api
    public HttpResponse<Book> testBookRepositoryById(@QueryValue long id) {
        return HttpResponse.ok(bookRepository.findNullable(id));
    }

    @Get("/testBookRepositoryByIdOptional")
    @Api
    public HttpResponse<Book> testBookRepositoryByIdOptional(@QueryValue long id) {
        if (bookRepository.findById(id).isPresent()) {
            return HttpResponse.ok(bookRepository.findById(id).get());
        } else {
            return HttpResponse.noContent();
        }
    }

    @Get("/testBookRepositoryByIdFetcher")
    @Api
    public @FetchBy("COMPLEX_BOOK") Book testBookRepositoryByIdFetcher(@QueryValue long id) {
        return bookRepository.findNullable(id, COMPLEX_BOOK);
    }

    @Get("/testBookRepositoryByIdFetcherOptional")
    @Api
    public @FetchBy("COMPLEX_BOOK") Book testBookRepositoryByIdFetcherOptional(
            @QueryValue long id) {
        if (bookRepository.findById(id, COMPLEX_BOOK).isPresent()) {
            return bookRepository.findById(id, COMPLEX_BOOK).get();
        } else {
            return null;
        }
    }

    @Get("/testBookRepositoryViewById")
    @Api
    public HttpResponse<BookDetailView> testBookRepositoryViewById(@QueryValue long id) {
        return HttpResponse.ok(bookRepository.viewer(BookDetailView.class).findNullable(id));
    }

    @Post("/testBookRepositoryFindAllById")
    @Api
    public HttpResponse<List<Book>> testBookRepositoryFindAllById(@Body List<Long> ids) {
        return HttpResponse.ok(bookRepository.findAllById(ids));
    }

    @Post("/testBookRepositoryFindByIdsFetcher")
    @Api
    public HttpResponse<List<Book>> testBookRepositoryFindByIdsFetcher(@Body List<Long> ids) {
        return HttpResponse.ok(bookRepository.findByIds(ids, COMPLEX_BOOK));
    }

    @Post("/testBookRepositoryFindMapByIds")
    @Api
    public HttpResponse<Map<Long, Book>> testBookRepositoryFindMapByIds(@Body List<Long> ids) {
        return HttpResponse.ok(bookRepository.findMapByIds(ids));
    }

    @Post("/testBookRepositoryFindMapByIdsFetcher")
    @Api
    public HttpResponse<Map<Long, Book>> testBookRepositoryFindMapByIdsFetcher(
            @Body List<Long> ids) {
        return HttpResponse.ok(bookRepository.findMapByIds(ids, COMPLEX_BOOK));
    }

    @Get("/testBookRepositoryFindAll")
    @Api
    public HttpResponse<List<Book>> testBookRepositoryFindAll() {
        return HttpResponse.ok(bookRepository.findAll());
    }

    @Get("/testBookRepositoryFindAllTypedPropScalar")
    @Api
    public HttpResponse<List<Book>> testBookRepositoryFindAllTypedPropScalar() {
        return HttpResponse.ok(bookRepository.findAll(BookProps.NAME.desc()));
    }

    @Get("/testBookRepositoryFindAllFetcherTypedPropScalar")
    @Api
    public HttpResponse<List<Book>> testBookRepositoryFindAllFetcherTypedPropScalar() {
        return HttpResponse.ok(bookRepository.findAll(COMPLEX_BOOK, BookProps.NAME.desc()));
    }

    @Get("/testBookRepositoryFindAllSort")
    @Api
    public HttpResponse<List<Book>> testBookRepositoryFindAllSort() {
        return HttpResponse.ok(bookRepository.findAll(Sort.of(Sort.Order.desc("name"))));
    }

    @Get("/testBookRepositoryFindAllFetcherSort")
    @Api
    public HttpResponse<List<Book>> testBookRepositoryFindAllFetcherSort() {
        return HttpResponse.ok(
                bookRepository.findAll(COMPLEX_BOOK, Sort.of(Sort.Order.desc("name"))));
    }

    @Post("/testBookRepositoryFindAllPageFetcher")
    @Api
    public HttpResponse<Page<Book>> testBookRepositoryFindAllPageFetcher(@Body Pageable pageable) {
        return HttpResponse.ok(
                bookRepository.findAll(pageable.getNumber(), pageable.getSize(), COMPLEX_BOOK));
    }

    @Post("/testBookRepositoryFindAllPageTypedPropScalar")
    @Api
    public HttpResponse<Page<Book>> testBookRepositoryFindAllPageTypedPropScalar(
            @Body Pageable pageable) {
        return HttpResponse.ok(
                bookRepository.findAll(
                        pageable.getNumber(), pageable.getSize(), BookProps.NAME.desc()));
    }

    @Post("/testBookRepositoryFindAllPageFetcherTypedPropScalar")
    @Api
    public HttpResponse<Page<Book>> testBookRepositoryFindAllPageFetcherTypedPropScalar(
            @Body Pageable pageable) {
        return HttpResponse.ok(
                bookRepository.findAll(
                        pageable.getNumber(),
                        pageable.getSize(),
                        COMPLEX_BOOK,
                        BookProps.NAME.desc()));
    }

    @Post("/testBookRepositoryFindAllPageSort")
    @Api
    public HttpResponse<Page<Book>> testBookRepositoryFindAllPageSort(@Body Pageable pageable) {
        return HttpResponse.ok(
                bookRepository.findAll(
                        pageable.getNumber(),
                        pageable.getSize(),
                        Sort.of(Sort.Order.desc("name"))));
    }

    @Post("/testBookRepositoryFindAllPageFetcherSort")
    @Api
    public HttpResponse<Page<Book>> testBookRepositoryFindAllPageFetcherSort(
            @Body Pageable pageable) {
        return HttpResponse.ok(
                bookRepository.findAll(
                        pageable.getNumber(),
                        pageable.getSize(),
                        COMPLEX_BOOK,
                        Sort.of(Sort.Order.desc("name"))));
    }

    @Get("/testBookRepositoryExistsById")
    @Api
    public HttpResponse<Boolean> testBookRepositoryExistsById(@QueryValue long id) {
        return HttpResponse.ok(bookRepository.existsById(id));
    }

    @Get("/testBookRepositoryCount")
    @Api
    public HttpResponse<Long> testBookRepositoryCount() {
        return HttpResponse.ok(bookRepository.count());
    }

    @Post("/testUserRoleRepositoryInsert")
    @Api
    @Transactional
    public HttpResponse<UserRole> testUserRoleRepositoryInsert(@Body UserRole userRole) {
        return HttpResponse.ok(userRoleRepository.insert(userRole));
    }

    @Post("/testUserRoleRepositoryInsertInput")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<UserRole> testUserRoleRepositoryInsertInput(
            @Body UserRoleInput userRoleInput) {
        return HttpResponse.ok(userRoleRepository.insert(userRoleInput));
    }

    @Post("/testUserRoleRepositorySave")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<UserRole> testUserRoleRepositorySave(@Body UserRole userRole) {
        return HttpResponse.ok(userRoleRepository.save(userRole));
    }

    @Post("/testUserRoleRepositorySaveInput")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<UserRole> testUserRoleRepositorySaveInput(
            @Body UserRoleInput userRoleInput) {
        return HttpResponse.ok(userRoleRepository.save(userRoleInput));
    }

    @Post("/testUserRoleRepositorySaveInputSaveMode")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<UserRole> testUserRoleRepositorySaveInputSaveMode(
            @Body UserRoleInput userRoleInput) {
        return HttpResponse.ok(userRoleRepository.save(userRoleInput, SaveMode.INSERT_ONLY));
    }

    @Post("/testUserRoleRepositorySaveCommand")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<SimpleEntitySaveCommand<UserRole>> testUserRoleRepositorySaveCommand(
            @Body UserRoleInput userRoleInput) {
        return HttpResponse.ok(userRoleRepository.saveCommand(userRoleInput));
    }

    @Post("/testUserRoleRepositorySaveEntities")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<List<UserRole>> testUserRoleRepositorySaveEntities(
            @Body List<UserRole> list) {
        return HttpResponse.ok(userRoleRepository.saveEntities(list));
    }

    @Post("/testUserRoleRepositorySaveEntitiesSaveMode")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<List<UserRole>> testUserRoleRepositorySaveEntitiesSaveMode(
            @Body List<UserRole> list) {
        return HttpResponse.ok(userRoleRepository.saveEntities(list, SaveMode.INSERT_ONLY));
    }

    @Post("/testUserRoleRepositorySaveEntitiesCommand")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<BatchEntitySaveCommand<UserRole>> testUserRoleRepositorySaveEntitiesCommand(
            @Body List<UserRole> list) {
        return HttpResponse.ok(userRoleRepository.saveEntitiesCommand(list));
    }

    @Delete("/testUserRoleRepositoryDeleteAll")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<Integer> testUserRoleRepositoryDeleteAll(List<UserRole> list) {
        return HttpResponse.ok(userRoleRepository.deleteAll(list, DeleteMode.AUTO));
    }

    @Post("/testUserRoleRepositoryUpdate")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<UserRole> testUserRoleRepositoryUpdate(@Body UserRole userRole) {
        return HttpResponse.ok(userRoleRepository.update(userRole));
    }

    @Get("/testUserRoleRepositoryById")
    @Api
    public HttpResponse<UserRole> testUserRoleRepositoryById(@QueryValue UUID id) {
        return HttpResponse.ok(userRoleRepository.findNullable(id));
    }

    @Put("/testUserRoleRepositoryUpdateInput")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<Void> testUserRoleRepositoryUpdateInput(@Body UserRoleInput userRoleInput) {
        userRoleRepository.update(userRoleInput);
        return HttpResponse.ok();
    }

    @Post("/testBookRepositoryFindByIdsView")
    @Api
    public HttpResponse<List<BookDetailView>> testBookRepositoryFindByIdsView(
            @Body List<Long> ids) {
        return HttpResponse.ok(bookRepository.viewer(BookDetailView.class).findByIds(ids));
    }

    @Get("/testBookRepositoryFindAllView")
    @Api
    public HttpResponse<List<BookDetailView>> testBookRepositoryFindAllView() {
        return HttpResponse.ok(bookRepository.viewer(BookDetailView.class).findAll());
    }

    @Get("/testBookRepositoryFindAllTypedPropScalarView")
    @Api
    public HttpResponse<List<BookDetailView>> testBookRepositoryFindAllTypedPropScalarView() {
        return HttpResponse.ok(
                bookRepository.viewer(BookDetailView.class).findAll(BookProps.NAME.desc()));
    }

    @Get("/testBookRepositoryFindAllSortView")
    @Api
    public HttpResponse<List<BookDetailView>> testBookRepositoryFindAllSortView() {
        return HttpResponse.ok(
                bookRepository
                        .viewer(BookDetailView.class)
                        .findAll(Sort.of(Sort.Order.desc("name"))));
    }

    @Post("/testBookRepositoryFindAllPageView")
    @Api
    public HttpResponse<Page<BookDetailView>> testBookRepositoryFindAllPageView(
            @Body Pageable pageable) {
        return HttpResponse.ok(
                bookRepository
                        .viewer(BookDetailView.class)
                        .findAll(pageable.getNumber(), pageable.getSize()));
    }

    @Post("/testBookRepositoryFindAllPageTypedPropScalarView")
    @Api
    public HttpResponse<Page<BookDetailView>> testBookRepositoryFindAllPageTypedPropScalarView(
            @Body Pageable pageable) {
        return HttpResponse.ok(
                bookRepository
                        .viewer(BookDetailView.class)
                        .findAll(pageable.getNumber(), pageable.getSize(), BookProps.NAME.desc()));
    }

    @Post("/testBookRepositoryFindAllPageSortView")
    @Api
    public HttpResponse<Page<BookDetailView>> testBookRepositoryFindAllPageSortView(
            @Body Pageable pageable) {
        return HttpResponse.ok(
                bookRepository
                        .viewer(BookDetailView.class)
                        .findAll(
                                pageable.getNumber(),
                                pageable.getSize(),
                                Sort.of(Sort.Order.desc("name"))));
    }

    @Get("/testBookRepositoryCustomQuery")
    @Api
    public HttpResponse<Book> testBookRepositoryCustomQuery(@QueryValue long id) {
        return HttpResponse.ok(bookRepository.selectBookById(id));
    }

    @Post("/testBookRepositoryFindMapByIdsView")
    @Api
    public HttpResponse<Map<Long, BookDetailView>> testBookRepositoryFindMapByIdsView(
            @Body List<Long> ids) {
        return HttpResponse.ok(bookRepository.viewer(BookDetailView.class).findMapByIds(ids));
    }

    @Post("/testBookRepositoryMerge")
    @Api
    @Transactional
    public HttpResponse<Book> testBookRepositoryMerge(@Body Book book) {
        return HttpResponse.ok(bookRepository.save(book, AssociatedSaveMode.MERGE));
    }

    @Post("/testBookRepositoryMergeInput")
    @Api
    @Transactional
    public HttpResponse<Book> testBookRepositoryMergeInput(@Body BookInput bookInput) {
        return HttpResponse.ok(bookRepository.save(bookInput, AssociatedSaveMode.MERGE));
    }

    @Post("/testBookRepositoryMergeSaveMode")
    @Api
    @Transactional
    public HttpResponse<Book> testBookRepositoryMergeSaveMode(@Body Book book) {
        return HttpResponse.ok(bookRepository.save(book, AssociatedSaveMode.APPEND_IF_ABSENT));
    }

    @Get("/testMicronautOrdersSortUtilsStringCodes")
    @Api
    public HttpResponse<List<Book>> testMicronautOrdersSortUtilsStringCodes(
            @QueryValue(defaultValue = "id desc") String sort) {
        List<Book> books =
                sqlClient
                        .createQuery(Tables.BOOK_TABLE)
                        .orderBy(
                                MicronautOrders.toOrders(
                                        Tables.BOOK_TABLE, SortUtils.toSort(true, sort)))
                        .select(Tables.BOOK_TABLE)
                        .execute();
        return HttpResponse.ok(books);
    }

    @Get("/testMicronautOrdersSortUtilsTypedPropScalarProps")
    @Api
    public HttpResponse<List<Book>> testMicronautOrdersSortUtilsTypedPropScalarProps() {
        List<Book> books =
                sqlClient
                        .createQuery(Tables.BOOK_TABLE)
                        .orderBy(
                                MicronautOrders.toOrders(
                                        Tables.BOOK_TABLE,
                                        SortUtils.toSort(true, BookProps.ID.asc())))
                        .select(Tables.BOOK_TABLE)
                        .execute();
        return HttpResponse.ok(books);
    }

    @Post("/testEvent")
    @Transactional
    @Api
    public HttpResponse<Void> testEvent() {
        sqlClient
                .createUpdate(Tables.BOOK_TABLE)
                .set(Tables.BOOK_TABLE.storeId(), 2L)
                .where(Tables.BOOK_TABLE.id().eq(7L))
                .execute();
        return HttpResponse.ok();
    }

    private static final Fetcher<Book> COMPLEX_BOOK =
            BOOK_FETCHER
                    .allScalarFields()
                    .store(BOOK_STORE_FETCHER.name())
                    .authors(AUTHOR_FETCHER.firstName().lastName());
}
