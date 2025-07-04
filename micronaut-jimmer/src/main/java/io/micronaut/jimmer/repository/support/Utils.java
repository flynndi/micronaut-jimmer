package io.micronaut.jimmer.repository.support;

import io.micronaut.data.model.Sort;
import io.micronaut.jimmer.cfg.support.MicronautConnectionManager;
import io.micronaut.jimmer.cfg.support.MicronautTransientResolverProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.PropExpression;
import org.babyfish.jimmer.sql.ast.impl.table.TableImplementor;
import org.babyfish.jimmer.sql.ast.query.Order;
import org.babyfish.jimmer.sql.ast.query.OrderMode;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.ast.table.spi.PropExpressionImplementor;
import org.babyfish.jimmer.sql.ast.table.spi.TableProxy;
import org.babyfish.jimmer.sql.meta.EmbeddedColumns;
import org.babyfish.jimmer.sql.meta.MetadataStrategy;
import org.babyfish.jimmer.sql.runtime.ConnectionManager;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;

public class Utils {

    private Utils() {}

    public static <E> Collection<E> toCollection(Iterable<E> iterable) {
        if (iterable instanceof Collection<?>) {
            return (Collection<E>) iterable;
        }
        if (iterable == null) {
            return Collections.emptyList();
        }
        List<E> list = new ArrayList<>();
        for (E e : iterable) {
            list.add(e);
        }
        return list;
    }

    public static JSqlClientImplementor validateSqlClient(JSqlClient sqlClient) {
        JSqlClientImplementor implementor = (JSqlClientImplementor) sqlClient;
        if (!(implementor.getTransientResolverProvider()
                instanceof MicronautTransientResolverProvider)) {
            throw new IllegalArgumentException(
                    "The transient resolver provider of sql client must be instance of \""
                            + MicronautTransientResolverProvider.class.getName()
                            + "\"");
        }
        ConnectionManager slaveConnectionManager = implementor.getSlaveConnectionManager(false);
        if (slaveConnectionManager != null
                && !(slaveConnectionManager instanceof MicronautConnectionManager)) {
            throw new IllegalArgumentException(
                    "The slave connection manager of sql client must be null or instance of \""
                            + MicronautConnectionManager.class.getName()
                            + "\"");
        }
        return implementor;
    }

    public static Sort toSort(List<Order> orders, MetadataStrategy strategy) {
        if (orders == null || orders.isEmpty()) {
            return Sort.unsorted();
        }
        List<Sort.Order> micronutOrders = new ArrayList<>(orders.size());
        for (Order order : orders) {
            if (order.getExpression() instanceof PropExpression<?>) {
                PropExpressionImplementor<?> propExpr =
                        (PropExpressionImplementor<?>) order.getExpression();
                String prefix = prefix(propExpr.getTable());
                EmbeddedColumns.Partial partial = propExpr.getPartial(strategy);
                String path = partial != null ? partial.path() : propExpr.getProp().getName();
                if (prefix != null) {
                    path = prefix + '.' + path;
                }
                micronutOrders.add(
                        new Sort.Order(
                                path,
                                order.getOrderMode() == OrderMode.DESC
                                        ? Sort.Order.Direction.DESC
                                        : Sort.Order.Direction.ASC,
                                false));
            }
        }
        return Sort.of(micronutOrders);
    }

    private static String prefix(Table<?> table) {
        ImmutableProp prop =
                table instanceof TableProxy<?>
                        ? ((TableProxy<?>) table).__prop()
                        : ((TableImplementor<?>) table).getJoinProp();
        if (prop == null) {
            return null;
        }

        String name = prop.getName();

        boolean inverse =
                table instanceof TableProxy<?>
                        ? ((TableProxy<?>) table).__isInverse()
                        : ((TableImplementor<?>) table).isInverse();
        if (inverse) {
            name = "[←" + name + ']';
        }

        Table<?> parent =
                table instanceof TableProxy<?>
                        ? ((TableProxy<?>) table).__parent()
                        : ((TableImplementor<?>) table).getParent();
        if (parent == null) {
            return name;
        }
        String parentPrefix = prefix(parent);
        if (parentPrefix == null) {
            return name;
        }
        return parentPrefix + '.' + name;
    }
}
