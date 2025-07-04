package io.micronaut.jimmer.kotlin.it.resolver

import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.kotlin.it.entity.BookStore
import io.micronaut.jimmer.kotlin.it.entity.edition
import io.micronaut.jimmer.kotlin.it.entity.id
import io.micronaut.jimmer.kotlin.it.entity.name
import io.micronaut.jimmer.kotlin.it.entity.storeId
import jakarta.inject.Singleton
import org.babyfish.jimmer.kt.toImmutableProp
import org.babyfish.jimmer.lang.Ref
import org.babyfish.jimmer.sql.TransientResolver
import org.babyfish.jimmer.sql.event.AssociationEvent
import org.babyfish.jimmer.sql.event.EntityEvent
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asNonNull
import org.babyfish.jimmer.sql.kt.ast.expression.max
import org.babyfish.jimmer.sql.kt.ast.expression.tuple
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.babyfish.jimmer.sql.kt.event.getUnchangedRef
import org.babyfish.jimmer.sql.kt.event.isChanged
import java.util.Collections
import java.util.SortedMap

@Singleton
class BookStoreNewestBooksResolver(
    private val sqlClient: KSqlClient,
) : TransientResolver<Long, List<Long>> {
    override fun resolve(ids: Collection<Long>): Map<Long, List<Long>> =
        sqlClient
            .createQuery(Book::class) {
                where(
                    tuple(table.storeId, table.name, table.edition) valueIn
                        subQuery(Book::class) {
                            where(table.storeId valueIn ids)
                            groupBy(table.storeId, table.name)
                            select(
                                table.storeId,
                                table.name,
                                max(table.edition).asNonNull(),
                            )
                        },
                )
                select(
                    table.storeId.asNonNull(),
                    table.id,
                )
            }.execute()
            .groupBy({ it._1 }) {
                it._2
            }

    override fun getDefaultValue(): List<Long> = Collections.emptyList()

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
            if (store !== null && e.isChanged(Book::edition)) {
                return listOf(store.id)
            }
        }
        return null
    }
}
