package io.micronaut.jimmer.repository;

import io.micronaut.data.model.Sort;
import java.util.ArrayList;
import java.util.List;
import org.babyfish.jimmer.meta.TypedProp;
import org.babyfish.jimmer.sql.ast.Expression;
import org.babyfish.jimmer.sql.ast.query.Order;
import org.babyfish.jimmer.sql.ast.table.Props;

public class MicronautOrders {

    private static final TypedProp.Scalar<?, ?>[] EMPTY_PROPS = new TypedProp.Scalar<?, ?>[0];

    private static final Order[] EMPTY_ORDERS = new Order[0];

    private MicronautOrders() {}

    public static Order[] toOrders(Props table, Sort sort) {
        if (null == sort) {
            return EMPTY_ORDERS;
        }
        List<Order> astOrders = new ArrayList<>();
        for (Sort.Order order : sort.getOrderBy()) {
            Expression<?> expr = Order.orderedExpression(table, order.getProperty());
            Order astOrder = order.isAscending() ? expr.asc() : expr.desc();
            astOrders.add(astOrder);
        }
        return astOrders.toArray(EMPTY_ORDERS);
    }
}
