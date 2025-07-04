package io.micronaut.jimmer.java.it.resolver;

import io.micronaut.jimmer.java.it.entity.*;
import jakarta.inject.Singleton;
import java.util.*;
import org.babyfish.jimmer.lang.Ref;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.TransientResolver;
import org.babyfish.jimmer.sql.ast.Expression;
import org.babyfish.jimmer.sql.ast.tuple.Tuple2;
import org.babyfish.jimmer.sql.event.AssociationEvent;
import org.babyfish.jimmer.sql.event.EntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class BookStoreNewestBooksResolver implements TransientResolver<Long, List<Long>> {

    private final JSqlClient sqlClient;

    public BookStoreNewestBooksResolver(JSqlClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    @Override
    public Map<Long, List<Long>> resolve(Collection<Long> ids) {
        return Tuple2.toMultiMap(
                sqlClient
                        .createQuery(Tables.BOOK_TABLE)
                        .where(
                                Expression.tuple(
                                                Tables.BOOK_TABLE.name(),
                                                Tables.BOOK_TABLE.edition())
                                        .in(
                                                sqlClient
                                                        .createSubQuery(Tables.BOOK_TABLE)
                                                        .where(Tables.BOOK_TABLE.storeId().in(ids))
                                                        .groupBy(Tables.BOOK_TABLE.name())
                                                        .select(
                                                                Tables.BOOK_TABLE.name(),
                                                                Tables.BOOK_TABLE.edition().max())))
                        .select(Tables.BOOK_TABLE.storeId(), Tables.BOOK_TABLE.id())
                        .execute());
    }

    @Override
    public List<Long> getDefaultValue() {
        return Collections.emptyList();
    }

    @Override
    public Ref<SortedMap<String, Object>> getParameterMapRef() {
        return sqlClient.getFilters().getTargetParameterMapRef(BookStoreProps.BOOKS);
    }

    @Nullable
    @Override
    public Collection<?> getAffectedSourceIds(@NotNull AssociationEvent e) {
        if (sqlClient.getCaches().isAffectedBy(e)
                && e.getImmutableProp() == BookStoreProps.BOOKS.unwrap()) {
            return Collections.singletonList(e.getSourceId());
        }
        return null;
    }

    @Nullable
    @Override
    public Collection<?> getAffectedSourceIds(@NotNull EntityEvent<?> e) {
        if (sqlClient.getCaches().isAffectedBy(e)
                && !e.isEvict()
                && e.getImmutableType().getJavaClass() == Book.class) {

            Ref<BookStore> storeRef = e.getUnchangedRef(BookProps.STORE);
            if (storeRef != null && storeRef.getValue() != null && e.isChanged(BookProps.EDITION)) {
                return Collections.singletonList(storeRef.getValue().id());
            }
        }
        return null;
    }
}
