package io.micronaut.jimmer.kotlin.it.config

import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import io.micronaut.data.connection.jdbc.advice.DelegatingDataSource
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.io.InputStreamReader
import javax.sql.DataSource

@Singleton
class AppLifecycleBean(
    private val dataSource: DataSource,
    @Named("DB2") private val dataSource2: DataSource,
) : ApplicationEventListener<StartupEvent> {
    override fun onApplicationEvent(event: StartupEvent?) {
        try {
            this.initH2DB1()
            this.initH2DB2()
        } catch (e: java.lang.Exception) {
            throw java.lang.RuntimeException(e)
        }
    }

    @Throws(Exception::class)
    private fun initH2DB1() {
        val unwrappedDataSource = (dataSource as DelegatingDataSource).getTargetDataSource()
        unwrappedDataSource.getConnection().use { con ->
            val inputStream =
                AppLifecycleBean::class.java
                    .getClassLoader()
                    .getResourceAsStream("h2-database.sql")
            if (inputStream == null) {
                throw RuntimeException("no `h2-database.sql`")
            }
            InputStreamReader(inputStream).use { reader ->
                val buf = CharArray(1024)
                val builder = StringBuilder()
                while (true) {
                    val len = reader.read(buf)
                    if (len == -1) {
                        break
                    }
                    builder.append(buf, 0, len)
                }
                con.createStatement().execute(builder.toString())
            }
        }
    }

    @Throws(Exception::class)
    private fun initH2DB2() {
        val unwrappedDataSource = (dataSource2 as DelegatingDataSource).getTargetDataSource()
        unwrappedDataSource.getConnection().use { con ->
            val inputStream =
                AppLifecycleBean::class.java
                    .getClassLoader()
                    .getResourceAsStream("h2-database2.sql")
            if (inputStream == null) {
                throw RuntimeException("no `h2-database2.sql`")
            }
            InputStreamReader(inputStream).use { reader ->
                val buf = CharArray(1024)
                val builder = StringBuilder()
                while (true) {
                    val len = reader.read(buf)
                    if (len == -1) {
                        break
                    }
                    builder.append(buf, 0, len)
                }
                con.createStatement().execute(builder.toString())
            }
        }
    }
}
