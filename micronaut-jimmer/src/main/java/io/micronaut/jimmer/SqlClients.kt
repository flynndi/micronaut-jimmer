package io.micronaut.jimmer

import io.micronaut.context.ApplicationContext
import org.babyfish.jimmer.sql.JSqlClient
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.cfg.KSqlClientDsl
import org.babyfish.jimmer.sql.kt.toKSqlClient
import java.util.function.Consumer
import javax.sql.DataSource
import kotlin.let

object SqlClients {
    @JvmStatic
    fun java(ctx: ApplicationContext): JSqlClient = java(ctx, null, null)

    @JvmStatic
    fun java(
        ctx: ApplicationContext,
        dataSource: DataSource?,
        dataSourceName: String?,
    ): JSqlClient = java(ctx, dataSource, dataSourceName, null)

    @JvmStatic
    fun java(
        ctx: ApplicationContext,
        block: Consumer<JSqlClient.Builder>?,
    ): JSqlClient = java(ctx, null, null, block)

    @JvmStatic
    fun java(
        ctx: ApplicationContext,
        dataSource: DataSource?,
        dataSourceName: String?,
        block: Consumer<JSqlClient.Builder>?,
    ): JSqlClient = JMicronautSqlClient(ctx, dataSource, dataSourceName, block, false)

    @JvmStatic
    fun kotlin(ctx: ApplicationContext): KSqlClient = kotlin(ctx, null, null)

    @JvmStatic
    fun kotlin(
        ctx: ApplicationContext,
        dataSource: DataSource?,
        dataSourceName: String?,
    ): KSqlClient = kotlin(ctx, dataSource, dataSourceName, null)

    @JvmStatic
    fun kotlin(
        ctx: ApplicationContext,
        block: (KSqlClientDsl.() -> Unit)?,
    ): KSqlClient = kotlin(ctx, null, null, block)

    @JvmStatic
    fun kotlin(
        ctx: ApplicationContext,
        dataSource: DataSource?,
        dataSourceName: String?,
        block: (KSqlClientDsl.() -> Unit)?,
    ): KSqlClient =
        JMicronautSqlClient(ctx, dataSource, dataSourceName, block?.let { Consumer { KSqlClientDsl(it).block() } }, true).toKSqlClient()
}
