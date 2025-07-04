package io.micronaut.jimmer.java.it.service.impl;

import io.micronaut.jimmer.java.it.entity.BookStore;
import io.micronaut.jimmer.java.it.entity.BookStoreTable;
import io.micronaut.jimmer.java.it.entity.Fetchers;
import io.micronaut.jimmer.java.it.entity.Tables;
import io.micronaut.jimmer.java.it.service.IBookStore;
import jakarta.inject.Singleton;
import java.util.List;
import org.babyfish.jimmer.sql.JSqlClient;

@Singleton
public class BookStoreImpl implements IBookStore {

    BookStoreTable table = Tables.BOOK_STORE_TABLE;

    private final JSqlClient sqlClient;

    public BookStoreImpl(JSqlClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    @Override
    public List<BookStore> oneToMany() {
        return sqlClient
                .createQuery(table)
                .select(
                        table.fetch(
                                Fetchers.BOOK_STORE_FETCHER
                                        .allScalarFields()
                                        .books(Fetchers.BOOK_FETCHER.allScalarFields())))
                .execute();
    }
}
