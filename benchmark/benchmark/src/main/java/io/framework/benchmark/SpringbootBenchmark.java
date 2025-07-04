package io.framework.benchmark;

import io.framework.benchmark.config.SpringDatabaseInitializer;
import io.framework.benchmark.entity.JimmerDataSpringbootTable;
import io.framework.benchmark.entity.Tables;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.babyfish.jimmer.sql.JSqlClient;
import org.openjdk.jmh.annotations.*;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

@State(Scope.Benchmark)
public class SpringbootBenchmark {

    private static final Map<String, Object> SPRINGBOOT_CONFIG;

    static {
        SPRINGBOOT_CONFIG = new HashMap<>();
        SPRINGBOOT_CONFIG.put("jimmer.language", "java");
        SPRINGBOOT_CONFIG.put("jimmer.dialect", "org.babyfish.jimmer.sql.dialect.H2Dialect");
        SPRINGBOOT_CONFIG.put("jimmer.showSql", false);
        SPRINGBOOT_CONFIG.put("jimmer.prettySql", false);
        SPRINGBOOT_CONFIG.put("jimmer.inlineSqlVariables", false);

        SPRINGBOOT_CONFIG.put(
                "spring.datasource.url",
                "jdbc:h2:mem:springtestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        SPRINGBOOT_CONFIG.put("spring.datasource.driver-class-name", "org.h2.Driver");
        SPRINGBOOT_CONFIG.put("spring.datasource.username", "sa");
        SPRINGBOOT_CONFIG.put("spring.datasource.hikari.pool-name", "SpringHikariPool");
        SPRINGBOOT_CONFIG.put("spring.datasource.hikari.maximum-pool-size", 10);
        SPRINGBOOT_CONFIG.put("spring.datasource.hikari.minimum-idle", 5);
        SPRINGBOOT_CONFIG.put("spring.datasource.hikari.idle-timeout", 30000);
        SPRINGBOOT_CONFIG.put("spring.datasource.hikari.max-lifetime", 1800000);
        SPRINGBOOT_CONFIG.put("spring.datasource.hikari.connection-timeout", 30000);
        SPRINGBOOT_CONFIG.put("spring.datasource.hikari.leak-detection-threshold", 0);
        SPRINGBOOT_CONFIG.put("spring.datasource.hikari.validation-timeout", 5000);
    }

    @Param({"10", "20", "50", "100", "200", "500", "1000"})
    private int dataCount;

    private ConfigurableApplicationContext springContext;

    private DataSource dataSource;

    @Setup
    public void initialize() throws SQLException, IOException {
        springContext =
                new SpringApplicationBuilder(SpringbootBenchmarkApplication.class)
                        .properties(SPRINGBOOT_CONFIG)
                        .run();
        dataSource = springContext.getBean(DataSource.class);
        SpringDatabaseInitializer springInitializer =
                springContext.getBean(SpringDatabaseInitializer.class);
        springInitializer.initialize(dataCount);
    }

    @Benchmark
    public void testSpringBoot() {
        JSqlClient sqlClient = springContext.getBean(JSqlClient.class);
        sqlClient
                .createQuery(Tables.JIMMER_DATA_SPRINGBOOT_TABLE)
                .select(JimmerDataSpringbootTable.$)
                .execute();
    }

    @Benchmark
    public void testSpringBootWithDatasource() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (Statement statement = connection.createStatement()) {
            statement.execute("SELECT * FROM data_springboot");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @TearDown
    public void tearDown() {
        if (springContext != null) {
            springContext.close();
        }
    }
}
