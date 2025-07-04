package io.micronaut.jimmer.java.it.service;

import io.micronaut.jimmer.java.it.entity.Book;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.babyfish.jimmer.sql.ast.mutation.SimpleSaveResult;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.jetbrains.annotations.Nullable;

public interface IBook {

    Book findById(long id);

    Book findById(int id, Fetcher<Book> fetcher);

    List<Book> findByIds(List<Integer> ids);

    SimpleSaveResult<Book> save(Book book);

    Map<Long, BigDecimal> findAvgPriceGroupByStoreId(Collection<Long> storeIds);

    List<Book> findBooksByName(@Nullable String name, @Nullable Fetcher<Book> fetcher);

    List<Book> findBooksByName(@Nullable String name);

    void update();

    List<Book> manyToMany();

    void updateOneToMany();

    void saveManyToMany();
}
