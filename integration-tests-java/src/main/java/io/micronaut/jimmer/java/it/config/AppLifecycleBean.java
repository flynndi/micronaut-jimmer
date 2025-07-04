package io.micronaut.jimmer.java.it.config;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.data.connection.jdbc.advice.DelegatingDataSource;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import javax.sql.DataSource;

@Singleton
public class AppLifecycleBean implements ApplicationEventListener<StartupEvent> {

    private final DataSource dataSource;

    private final DataSource dataSource2;

    public AppLifecycleBean(DataSource dataSource, @Named("DB2") DataSource dataSource2) {
        this.dataSource = dataSource;
        this.dataSource2 = dataSource2;
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        try {
            this.initH2DB1();
            this.initH2DB2();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initH2DB1() throws Exception {
        DataSource unwrappedDataSource = ((DelegatingDataSource) dataSource).getTargetDataSource();
        try (Connection con = unwrappedDataSource.getConnection()) {
            InputStream inputStream =
                    AppLifecycleBean.class.getClassLoader().getResourceAsStream("h2-database.sql");
            if (inputStream == null) {
                throw new RuntimeException("no `h2-database.sql`");
            }
            try (Reader reader = new InputStreamReader(inputStream)) {
                char[] buf = new char[1024];
                StringBuilder builder = new StringBuilder();
                while (true) {
                    int len = reader.read(buf);
                    if (len == -1) {
                        break;
                    }
                    builder.append(buf, 0, len);
                }
                con.createStatement().execute(builder.toString());
            }
        }
    }

    private void initH2DB2() throws Exception {
        DataSource unwrappedDataSource = ((DelegatingDataSource) dataSource2).getTargetDataSource();
        try (Connection con = unwrappedDataSource.getConnection()) {
            InputStream inputStream =
                    AppLifecycleBean.class.getClassLoader().getResourceAsStream("h2-database2.sql");
            if (inputStream == null) {
                throw new RuntimeException("no `h2-database2.sql`");
            }
            try (Reader reader = new InputStreamReader(inputStream)) {
                char[] buf = new char[1024];
                StringBuilder builder = new StringBuilder();
                while (true) {
                    int len = reader.read(buf);
                    if (len == -1) {
                        break;
                    }
                    builder.append(buf, 0, len);
                }
                con.createStatement().execute(builder.toString());
            }
        }
    }
}
