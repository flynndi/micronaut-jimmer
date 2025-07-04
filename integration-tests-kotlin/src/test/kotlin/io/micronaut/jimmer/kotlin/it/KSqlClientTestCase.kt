package io.micronaut.jimmer.kotlin.it

import io.micronaut.context.ApplicationContext
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.jimmer.kotlin.it.config.TenantFilter
import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.kotlin.it.entity.UserRole
import io.micronaut.jimmer.kotlin.it.entity.by
import io.micronaut.jimmer.kotlin.it.entity.id
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Named
import org.babyfish.jimmer.sql.filter.Filters
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

@MicronautTest
class KSqlClientTestCase {
    @Inject
    lateinit var kSqlClient: KSqlClient

    @Inject
    @Named(Constant.DATASOURCE2)
    lateinit var kSqlClientDB2: KSqlClient

    @Inject
    lateinit var applicationContext: ApplicationContext

    @Test
    fun testBean() {
        Assertions.assertEquals(
            kSqlClient,
            applicationContext.getBean(
                KSqlClient::class.java,
                Qualifiers.byName(Constant.DEFAULT),
            ),
        )
        Assertions.assertEquals(
            kSqlClientDB2,
            applicationContext.getBean(
                KSqlClient::class.java,
                Qualifiers.byName(Constant.DATASOURCE2),
            ),
        )
        Assertions.assertNotEquals(kSqlClient, kSqlClientDB2)
        Assertions.assertNotEquals(
            kSqlClient,
            applicationContext.getBean(
                KSqlClient::class.java,
                Qualifiers.byName(Constant.DATASOURCE2),
            ),
        )
        Assertions.assertNotEquals(
            kSqlClientDB2,
            applicationContext.getBean(
                KSqlClient::class.java,
                Qualifiers.byName(Constant.DEFAULT),
            ),
        )
    }

    @Test
    fun testTenantFilter() {
        val filters: Filters = kSqlClient.javaClient.filters
        val filter = filters.getFilter(Book::class.java)
        val tenantFilterOptional = applicationContext.findBean(TenantFilter::class.java)
        Assertions.assertTrue(tenantFilterOptional.isPresent)
        Assertions.assertNotNull(tenantFilterOptional.get())
        val tenantFilter: TenantFilter = tenantFilterOptional.get()
        Assertions.assertEquals(
            filter
                .toString()
                .replace(
                    "ExportedFilter{filters=[",
                    "",
                ).replace("]}", "")
                .replace("JavaFilter(kotlinFilter=", "")
                .replace(")", ""),
            tenantFilter.toString(),
        )
    }

    @Test
    fun testScalarProvider() {
        val userRole =
            kSqlClientDB2
                .createQuery(UserRole::class) {
                    where(table.id eq UUID.fromString(Constant.USER_ROLE_ID))
                    select(
                        table.fetch(
                            newFetcher(UserRole::class).by {
                                allTableFields()
                            },
                        ),
                    )
                }.fetchOne()
        Assertions.assertNotNull(userRole)
        Assertions.assertEquals(userRole.id.javaClass.getTypeName(), UUID::class.java.getName())
    }
}
