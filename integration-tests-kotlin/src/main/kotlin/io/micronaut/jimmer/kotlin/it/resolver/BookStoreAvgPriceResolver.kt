package io.micronaut.jimmer.kotlin.it.resolver

import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.kotlin.it.entity.BookStore
import io.micronaut.jimmer.kotlin.it.service.IBook
import jakarta.inject.Singleton
import org.babyfish.jimmer.kt.toImmutableProp
import org.babyfish.jimmer.lang.Ref
import org.babyfish.jimmer.sql.event.AssociationEvent
import org.babyfish.jimmer.sql.event.EntityEvent
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.KTransientResolver
import org.babyfish.jimmer.sql.kt.event.getUnchangedRef
import org.babyfish.jimmer.sql.kt.event.isChanged
import java.math.BigDecimal
import java.util.SortedMap

@Singleton
class BookStoreAvgPriceResolver(
    private val sqlClient: KSqlClient,
    private val iBook: IBook,
) : KTransientResolver<Long, BigDecimal> {
    override fun resolve(ids: Collection<Long>): Map<Long, BigDecimal> = iBook.findAvgPriceGroupByStoreId(ids)

    override fun getDefaultValue(): BigDecimal = BigDecimal.ZERO

    override fun getParameterMapRef(): Ref<SortedMap<String, Any>?>? = sqlClient.filters.getTargetParameterMapRef(BookStore::books)

    override fun getAffectedSourceIds(e: AssociationEvent): Collection<*>? =
        if (sqlClient.caches.isAffectedBy(e) && e.immutableProp === BookStore::books.toImmutableProp()) {
            listOf(e.sourceId)
        } else {
            null
        }

    override fun getAffectedSourceIds(e: EntityEvent<*>): Collection<*>? {
        if (sqlClient.caches.isAffectedBy(e) &&
            !e.isEvict &&
            e.getImmutableType().javaClass == Book::class.java
        ) {
            val store = e.getUnchangedRef(Book::store)?.value
            if (store !== null && e.isChanged(Book::price)) {
                return listOf(store.id)
            }
        }
        return null
    }
}
