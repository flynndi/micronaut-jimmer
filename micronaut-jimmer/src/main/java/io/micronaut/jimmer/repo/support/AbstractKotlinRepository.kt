package io.micronaut.jimmer.repo.support

import io.micronaut.core.reflect.GenericTypeUtils
import io.micronaut.jimmer.repo.KotlinRepository
import io.micronaut.jimmer.repo.PageParam
import io.micronaut.jimmer.repository.orderBy
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.Slice
import org.babyfish.jimmer.View
import org.babyfish.jimmer.meta.ImmutableType
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.babyfish.jimmer.sql.ast.mutation.DeleteMode
import org.babyfish.jimmer.sql.fetcher.DtoMetadata
import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.KExecutable
import org.babyfish.jimmer.sql.kt.ast.mutation.KBatchEntitySaveCommand
import org.babyfish.jimmer.sql.kt.ast.mutation.KMutableDelete
import org.babyfish.jimmer.sql.kt.ast.mutation.KMutableUpdate
import org.babyfish.jimmer.sql.kt.ast.mutation.KSaveCommandDsl
import org.babyfish.jimmer.sql.kt.ast.mutation.KSimpleEntitySaveCommand
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery
import org.babyfish.jimmer.sql.kt.ast.query.KMutableRootQuery
import org.babyfish.jimmer.sql.kt.ast.query.SortDsl
import kotlin.reflect.KClass

/**
 * The base implementation of [KotlinRepository]
 *
 * If the repository
 */
abstract class AbstractKotlinRepository<E : Any, ID : Any>(
    protected val sql: KSqlClient,
) : KotlinRepository<E, ID> {
    @Suppress("UNCHECKED_CAST")
    protected val entityType: KClass<E> =
        GenericTypeUtils
            .resolveSuperTypeGenericArguments(
                this.javaClass,
                AbstractKotlinRepository::class.java,
            )?.let { it[0].kotlin as KClass<E> }
            ?: throw IllegalArgumentException(
                "The class \"" + this.javaClass + "\" " +
                    "does not explicitly specify the type arguments of \"" +
                    KotlinRepository::class.java.name +
                    "\" so that the entityType must be specified",
            )

    protected val immutableType: ImmutableType =
        ImmutableType.get(this.entityType.java)

    override fun findById(
        id: ID,
        fetcher: Fetcher<E>?,
    ): E? =
        if (fetcher == null) {
            sql.findById(entityType, id)
        } else {
            sql.findById(fetcher, id)
        }

    override fun <V : View<E>> findById(
        id: ID,
        viewType: KClass<V>,
    ): V? = sql.findById(viewType, id)

    override fun findByIds(
        ids: Iterable<ID>,
        fetcher: Fetcher<E>?,
    ): List<E> =
        if (fetcher ==
            null
        ) {
            sql.findByIds(entityType, ids)
        } else {
            sql.findByIds(fetcher, ids)
        }

    override fun <V : View<E>> findByIds(
        ids: Iterable<ID>,
        viewType: KClass<V>,
    ): List<V> = sql.findByIds(viewType, ids)

    override fun findMapByIds(
        ids: Iterable<ID>,
        fetcher: Fetcher<E>?,
    ): Map<ID, E> =
        if (fetcher ==
            null
        ) {
            sql.findMapByIds(entityType, ids)
        } else {
            sql.findMapByIds(fetcher, ids)
        }

    @Suppress("UNCHECKED_CAST")
    override fun <V : View<E>> findMapByIds(
        ids: Iterable<ID>,
        viewType: KClass<V>,
    ): Map<ID, V> =
        DtoMetadata.of(viewType.java).let { metadata ->
            val idPropId = immutableType.idProp.id
            sql.findByIds(metadata.fetcher, ids).associateBy({
                (it as ImmutableSpi).__get(idPropId) as ID
            }) {
                metadata.converter.apply(it)
            }
        }

    override fun findAll(
        fetcher: Fetcher<E>?,
        block: (SortDsl<E>.() -> Unit)?,
    ): List<E> =
        if (fetcher ==
            null
        ) {
            sql.entities.findAll(entityType, block)
        } else {
            sql.entities.findAll(fetcher, block)
        }

    override fun <V : View<E>> findAll(
        viewType: KClass<V>,
        block: (SortDsl<E>.() -> Unit)?,
    ): List<V> = sql.entities.findAllViews(viewType, block)

    override fun findPage(
        pageParam: PageParam,
        fetcher: Fetcher<E>?,
        block: (SortDsl<E>.() -> Unit)?,
    ): Page<E> =
        sql
            .createQuery(entityType) {
                orderBy(block)
                select(table.fetch(fetcher))
            }.fetchPage(pageParam.index, pageParam.size)

    override fun <V : View<E>> findPage(
        pageParam: PageParam,
        viewType: KClass<V>,
        block: (SortDsl<E>.() -> Unit)?,
    ): Page<V> =
        sql
            .createQuery(entityType) {
                orderBy(block)
                select(table.fetch(viewType))
            }.fetchPage(pageParam.index, pageParam.size)

    override fun findSlice(
        limit: Int,
        offset: Int,
        fetcher: Fetcher<E>?,
        block: (SortDsl<E>.() -> Unit)?,
    ): Slice<E> =
        sql
            .createQuery(entityType) {
                orderBy(block)
                select(table.fetch(fetcher))
            }.fetchSlice(limit, offset)

    override fun <V : View<E>> findSlice(
        limit: Int,
        offset: Int,
        viewType: KClass<V>,
        block: (SortDsl<E>.() -> Unit)?,
    ): Slice<V> =
        sql
            .createQuery(entityType) {
                orderBy(block)
                select(table.fetch(viewType))
            }.fetchSlice(limit, offset)

    override fun saveCommand(
        entity: E,
        block: (KSaveCommandDsl.() -> Unit)?,
    ): KSimpleEntitySaveCommand<E> = sql.saveCommand(entity, block)

    override fun saveEntitiesCommand(
        entities: Iterable<E>,
        block: (KSaveCommandDsl.() -> Unit)?,
    ): KBatchEntitySaveCommand<E> = sql.saveEntitiesCommand(entities, block)

    override fun deleteById(
        id: ID,
        deleteMode: DeleteMode,
    ): Int = sql.deleteById(entityType, id, deleteMode).affectedRowCount(entityType)

    override fun deleteByIds(
        ids: Iterable<ID>,
        deleteMode: DeleteMode,
    ): Int = sql.deleteByIds(entityType, ids, deleteMode).affectedRowCount(entityType)

    protected fun <R> executeQuery(block: KMutableRootQuery<E>.() -> KConfigurableRootQuery<E, R>): List<R> =
        sql.createQuery(entityType, block).execute()

    protected fun <R> createQuery(block: KMutableRootQuery<E>.() -> KConfigurableRootQuery<E, R>): KConfigurableRootQuery<E, R> =
        sql.createQuery(entityType, block)

    protected fun createUpdate(block: KMutableUpdate<E>.() -> Unit): KExecutable<Int> = sql.createUpdate(entityType, block)

    protected fun createDelete(block: KMutableDelete<E>.() -> Unit): KExecutable<Int> = sql.createDelete(entityType, block)
}
