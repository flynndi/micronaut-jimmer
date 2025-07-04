package io.micronaut.jimmer.repository.support;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import java.util.List;
import org.babyfish.jimmer.sql.ast.impl.query.PageSource;
import org.babyfish.jimmer.sql.ast.query.PageFactory;

public class MicronautPageFactory<E> implements PageFactory<E, Page<E>> {

    private static final MicronautPageFactory<?> INSTANCE = new MicronautPageFactory<>();

    private MicronautPageFactory() {}

    @Override
    public Page<E> create(List<E> entities, long totalCount, PageSource source) {
        return Page.of(
                entities,
                Pageable.from(source.getPageIndex(), source.getPageSize())
                        .withSort(
                                Utils.toSort(
                                        source.getOrders(),
                                        source.getSqlClient().getMetadataStrategy())),
                totalCount);
    }

    @SuppressWarnings("unchecked")
    public static <E> MicronautPageFactory<E> getInstance() {
        return (MicronautPageFactory<E>) INSTANCE;
    }
}
