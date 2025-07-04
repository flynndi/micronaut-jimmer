package io.micronaut.jimmer.kotlin.it.config

import io.micronaut.jimmer.kotlin.it.entity.TenantAware
import io.micronaut.jimmer.kotlin.it.entity.tenant
import jakarta.inject.Singleton
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.filter.KFilter
import org.babyfish.jimmer.sql.kt.filter.KFilterArgs

@Singleton
class TenantFilter(
    private val tenantProvider: TenantProvider,
) : KFilter<TenantAware> {
    override fun filter(args: KFilterArgs<TenantAware>) {
        tenantProvider.tenant.let {
            args.apply {
                where(table.tenant.eq(it))
            }
        }
    }
}
