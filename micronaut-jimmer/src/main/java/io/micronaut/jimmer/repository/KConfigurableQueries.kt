package io.micronaut.jimmer.repository

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.jimmer.repository.support.MicronautPageFactory
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery
import java.sql.Connection

fun <E> KConfigurableRootQuery<*, E>.fetchMicronautPage(
    pageIndex: Int,
    pageSize: Int,
    con: Connection? = null,
): Page<E> =
    fetchPage(
        pageIndex,
        pageSize,
        con,
        MicronautPageFactory.getInstance(),
    )

fun <E> KConfigurableRootQuery<*, E>.fetchMicronautPage(
    pageable: Pageable?,
    con: Connection? = null,
): Page<E> =
    if (pageable === null || pageable.isUnpaged) {
        fetchMicronautPage(0, Int.MAX_VALUE, con)
    } else {
        fetchMicronautPage(pageable.number, pageable.size, con)
    }
