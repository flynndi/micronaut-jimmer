package io.micronaut.jimmer.java.it.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.FileUpload;
import io.micronaut.jimmer.java.it.config.error.UserInfoException;
import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.java.it.entity.Fetchers;
import io.micronaut.jimmer.java.it.entity.Tables;
import io.micronaut.jimmer.java.it.entity.dto.BookDetailView;
import io.micronaut.jimmer.java.it.entity.dto.BookSpecification;
import io.micronaut.jimmer.java.it.service.IBook;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.Api;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SimpleSaveResult;
import org.babyfish.jimmer.sql.fetcher.Fetcher;

@Controller("/bookResource")
@Api
public class BookResources implements Fetchers {

    private final IBook iBook;

    private final JSqlClient sqlClient;

    public BookResources(IBook iBook, JSqlClient sqlClient) {
        this.iBook = iBook;
        this.sqlClient = sqlClient;
    }

    @Get("/book")
    @Api
    public HttpResponse<Book> getBookById(@QueryValue long id) {
        int i = 1 / 0;
        return HttpResponse.ok(iBook.findById(id));
    }

    @Get("/book/{id}")
    @Api
    public @FetchBy("COMPLEX_BOOK") Book getBookByIdFetcher(@PathVariable int id) {
        return iBook.findById(id, COMPLEX_BOOK);
    }

    @Post("/books")
    @Api
    public HttpResponse<List<Book>> getBookByIds(List<Integer> ids) {
        return HttpResponse.ok(iBook.findByIds(ids));
    }

    @Post("/book")
    @Transactional(rollbackOn = Exception.class)
    @Api
    public HttpResponse<SimpleSaveResult<Book>> postBook(Book book) {
        return HttpResponse.ok(iBook.save(book));
    }

    @Get("/books")
    @Api
    public List<@FetchBy("SIMPLE_BOOK") Book> getBookByNameFetcher(@QueryValue String name) {
        return iBook.findBooksByName(name, SIMPLE_BOOK);
    }

    @Get("/booksByName")
    @Api
    public List<Book> getBookByName(@QueryValue String name) {
        return iBook.findBooksByName(name);
    }

    @Put("/update")
    @Api
    public HttpResponse<Void> update() {
        iBook.update();
        return HttpResponse.ok();
    }

    @Get("/testManyToMany")
    @Api
    public HttpResponse<List<Book>> testManyToMany() {
        return HttpResponse.ok(iBook.manyToMany());
    }

    @Put("/testUpdateOneToMany")
    @Api
    public HttpResponse<Void> testUpdateOneToMany() {
        iBook.updateOneToMany();
        return HttpResponse.ok();
    }

    @Post("/testSaveManyToMany")
    @Api
    public HttpResponse<Void> testSaveManyToMany() {
        iBook.saveManyToMany();
        return HttpResponse.ok();
    }

    @Post("/testFile")
    @Api
    public HttpResponse<Void> testFile(FileUpload filePart) {
        return HttpResponse.ok();
    }

    @Get("/testError")
    @Api
    public HttpResponse<Void> testError() throws UserInfoException.IllegalUserName {
        List<Character> illegalChars = new ArrayList<>();
        illegalChars.add('a');
        throw UserInfoException.illegalUserName("testError", illegalChars);
    }

    @Get("/testBookSpecification")
    @Api
    public HttpResponse<List<BookDetailView>> testBoolSpecification(
            @RequestBean BookSpecification bookSpecification) {
        return HttpResponse.ok(
                sqlClient
                        .createQuery(Tables.BOOK_TABLE)
                        .where(bookSpecification)
                        .select(Tables.BOOK_TABLE.fetch(BookDetailView.class))
                        .execute());
    }

    private static final Fetcher<Book> SIMPLE_BOOK = BOOK_FETCHER.name();

    private static final Fetcher<Book> COMPLEX_BOOK =
            BOOK_FETCHER
                    .allScalarFields()
                    .store(BOOK_STORE_FETCHER.name())
                    .authors(AUTHOR_FETCHER.firstName().lastName());
}
