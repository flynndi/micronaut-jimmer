package io.micronaut.jimmer.java.it.resolver;

import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.java.it.entity.BookProps;
import io.micronaut.jimmer.java.it.entity.BookStore;
import io.micronaut.jimmer.java.it.entity.BookStoreProps;
import io.micronaut.jimmer.java.it.service.IBook;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import org.babyfish.jimmer.lang.Ref;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.TransientResolver;
import org.babyfish.jimmer.sql.event.AssociationEvent;
import org.babyfish.jimmer.sql.event.EntityEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class BookStoreAvgPriceResolver implements TransientResolver<Long, BigDecimal> {

    private final IBook iBook;

    private final JSqlClient sqlClient;

    public BookStoreAvgPriceResolver(IBook iBook, JSqlClient sqlClient) {
        this.iBook = iBook;
        this.sqlClient = sqlClient;
    }

    @Override
    public Map<Long, BigDecimal> resolve(Collection<Long> ids) {
        return iBook.findAvgPriceGroupByStoreId(ids);
    }

    @Override
    public BigDecimal getDefaultValue() { // ❸
        return BigDecimal.ZERO;
    }

    @Override
    public Ref<SortedMap<String, Object>> getParameterMapRef() {
        return sqlClient.getFilters().getTargetParameterMapRef(BookStoreProps.BOOKS);
    }

    @Override
    public Collection<?> getAffectedSourceIds(@NotNull AssociationEvent e) {
        if (sqlClient.getCaches().isAffectedBy(e)
                && e.getImmutableProp() == BookStoreProps.BOOKS.unwrap()) {
            return Collections.singletonList(e.getSourceId());
        }
        return null;
    }

    @Override
    public Collection<?> getAffectedSourceIds(@NotNull EntityEvent<?> e) {
        if (sqlClient.getCaches().isAffectedBy(e)
                && !e.isEvict()
                && e.getImmutableType().getJavaClass() == Book.class) {

            Ref<BookStore> storeRef = e.getUnchangedRef(BookProps.STORE);
            if (storeRef != null && storeRef.getValue() != null && e.isChanged(BookProps.PRICE)) {
                return Collections.singletonList(storeRef.getValue().id());
            }
        }
        return null;
    }
}
