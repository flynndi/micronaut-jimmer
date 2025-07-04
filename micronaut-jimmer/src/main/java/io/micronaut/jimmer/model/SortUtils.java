package io.micronaut.jimmer.model;

import io.micronaut.data.model.Sort;
import java.util.ArrayList;
import java.util.List;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.meta.TypedProp;
import org.babyfish.jimmer.sql.ast.query.Order;
import org.babyfish.jimmer.sql.ast.query.OrderMode;

public class SortUtils {

    private SortUtils() {}

    public static Sort toSort(boolean ignoreCase, String... codes) {
        return Sort.of(
                Order.makeCustomOrders(
                        (path, orderMode, nullOrderMode) ->
                                new Sort.Order(
                                        path,
                                        orderMode == OrderMode.DESC
                                                ? Sort.Order.Direction.DESC
                                                : Sort.Order.Direction.ASC,
                                        ignoreCase),
                        codes));
    }

    public static Sort toSort(boolean ignoreCase, TypedProp.Scalar<?, ?>... props) {
        List<Sort.Order> orders = new ArrayList<>();
        ImmutableType entityType = null;
        for (TypedProp.Scalar<?, ?> prop : props) {
            ImmutableProp ip = prop.unwrap();
            ImmutableType dt = prop.unwrap().getDeclaringType();
            if (dt.isEntity()) {
                if (entityType != null && entityType != dt) {
                    throw new IllegalArgumentException("props do not belong to one entity type");
                }
                entityType = dt;
            }
            Sort.Order order =
                    new Sort.Order(
                            ip.getName(),
                            prop.isDesc() ? Sort.Order.Direction.DESC : Sort.Order.Direction.ASC,
                            ignoreCase);
            orders.add(order);
        }
        return Sort.of(orders);
    }
}
