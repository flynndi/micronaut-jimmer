package io.micronaut.jimmer.java.it.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.jimmer.java.it.entity.BookStore;
import io.micronaut.jimmer.java.it.entity.Fetchers;
import io.micronaut.jimmer.java.it.repository.BookStoreRepository;
import io.micronaut.jimmer.java.it.service.IBookStore;
import java.util.List;

@Controller("/bookStoreResource")
public class BookStoreResources {

    private final IBookStore iBookStore;

    private final BookStoreRepository bookStoreRepository;

    public BookStoreResources(IBookStore iBookStore, BookStoreRepository bookStoreRepository) {
        this.iBookStore = iBookStore;
        this.bookStoreRepository = bookStoreRepository;
    }

    @Get("test1")
    public HttpResponse<List<BookStore>> test1() {
        return HttpResponse.ok(iBookStore.oneToMany());
    }

    @Get("testNewestBooks")
    public HttpResponse<List<BookStore>> testNewestBooks() {
        return HttpResponse.ok(
                bookStoreRepository.findAll(
                        Fetchers.BOOK_STORE_FETCHER
                                .name()
                                .newestBooks(
                                        Fetchers.BOOK_FETCHER
                                                .allScalarFields()
                                                .authors(
                                                        Fetchers.AUTHOR_FETCHER
                                                                .allScalarFields()))));
    }
}
