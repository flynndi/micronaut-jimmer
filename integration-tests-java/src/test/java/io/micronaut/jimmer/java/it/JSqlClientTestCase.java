package io.micronaut.jimmer.java.it;

import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.jimmer.java.it.config.TenantFilter;
import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.java.it.entity.Fetchers;
import io.micronaut.jimmer.java.it.entity.UserRole;
import io.micronaut.jimmer.java.it.entity.UserRoleTable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Optional;
import java.util.UUID;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.table.Props;
import org.babyfish.jimmer.sql.filter.Filter;
import org.babyfish.jimmer.sql.filter.Filters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class JSqlClientTestCase {

    @Inject JSqlClient jSqlClient;

    @Inject
    @Named(Constant.DATASOURCE2)
    JSqlClient jSqlClientDB2;

    @Inject ApplicationContext applicationContext;

    @Test
    public void testBean() {
        Assertions.assertEquals(
                jSqlClient,
                applicationContext.getBean(JSqlClient.class, Qualifiers.byName(Constant.DEFAULT)));
        Assertions.assertEquals(
                jSqlClientDB2,
                applicationContext.getBean(
                        JSqlClient.class, Qualifiers.byName(Constant.DATASOURCE2)));
        Assertions.assertNotEquals(jSqlClient, jSqlClientDB2);
        Assertions.assertNotEquals(
                jSqlClient,
                applicationContext.getBean(
                        JSqlClient.class, Qualifiers.byName(Constant.DATASOURCE2)));
        Assertions.assertNotEquals(
                jSqlClientDB2,
                applicationContext.getBean(JSqlClient.class, Qualifiers.byName(Constant.DEFAULT)));
    }

    @Test
    public void testTenantFilter() {
        Filters filters = jSqlClient.getFilters();
        Filter<Props> filter = filters.getFilter(Book.class);
        Optional<TenantFilter> tenantFilterOptional =
                applicationContext.findBean(TenantFilter.class);
        Assertions.assertTrue(tenantFilterOptional.isPresent());
        Assertions.assertNotNull(tenantFilterOptional.get());
        TenantFilter tenantFilter = tenantFilterOptional.get();
        Assertions.assertEquals(
                filter.toString().replace("ExportedFilter{filters=[", "").replace("]}", ""),
                tenantFilter.toString());
    }

    @Test
    public void testScalarProvider() {
        UserRole userRole =
                jSqlClientDB2
                        .createQuery(UserRoleTable.$)
                        .where(UserRoleTable.$.id().eq(UUID.fromString(Constant.USER_ROLE_ID)))
                        .select(UserRoleTable.$.fetch(Fetchers.USER_ROLE_FETCHER.allTableFields()))
                        .fetchOne();
        Assertions.assertNotNull(userRole);
        Assertions.assertEquals(userRole.id().getClass().getTypeName(), UUID.class.getName());
    }
}
