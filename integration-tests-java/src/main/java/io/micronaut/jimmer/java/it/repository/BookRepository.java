package io.micronaut.jimmer.java.it.repository;

import io.micronaut.data.model.Pageable;
import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.java.it.entity.Tables;
import io.micronaut.jimmer.repository.DynamicParam;
import io.micronaut.jimmer.repository.JRepository;
import io.micronaut.jimmer.repository.annotation.Repository;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.ast.Expression;
import org.babyfish.jimmer.sql.ast.tuple.Tuple2;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.jetbrains.annotations.Nullable;

@Repository
public interface BookRepository extends JRepository<Book, Long> {

    default Book selectBookById(long id) {
        return sql().findById(Book.class, id);
    }

    default Map<Long, List<Long>> findNewestIdsGroupByStoreId(Collection<Long> ids) {
        return Tuple2.toMultiMap(
                sql().createQuery(Tables.BOOK_TABLE)
                        .where(
                                Expression.tuple(
                                                Tables.BOOK_TABLE.name(),
                                                Tables.BOOK_TABLE.edition())
                                        .in(
                                                sql().createSubQuery(Tables.BOOK_TABLE)
                                                        .where(Tables.BOOK_TABLE.storeId().in(ids))
                                                        .groupBy(Tables.BOOK_TABLE.name())
                                                        .select(
                                                                Tables.BOOK_TABLE.name(),
                                                                Tables.BOOK_TABLE.edition().max())))
                        .select(Tables.BOOK_TABLE.storeId(), Tables.BOOK_TABLE.id())
                        .execute());
    }

    Book findByNameAndEditionAndPrice(
            String name, int edition, BigDecimal price, Fetcher<Book> fetcher);

    List<Book> findByNameLike(String name, Fetcher<Book> fetcher);

    List<Book> findByStoreId(Long storeId, Fetcher<Book> fetcher);

    Page<Book> findByNameLikeOrderByName(String name, Pageable pageable, Fetcher<Book> fetcher);

    Page<Book> findByNameLikeIgnoreCaseAndStoreNameOrderByNameAscEditionDesc(
            Pageable pageable,
            @Nullable Fetcher<Book> fetcher,
            @DynamicParam @Nullable String name,
            @Nullable String storeName);
}
