package io.framework.benchmark;

import io.framework.benchmark.config.MicronautDatabaseInitializer;
import io.framework.benchmark.entity.JimmerDataMicronautTable;
import io.framework.benchmark.entity.Tables;
import io.micronaut.context.ApplicationContext;
import io.micronaut.data.connection.ConnectionDefinition;
import io.micronaut.data.connection.ConnectionOperations;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.babyfish.jimmer.sql.JSqlClient;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class MicronautBenchmark {

    private static final Map<String, Object> MICRONAUT_CONFIG;

    static {
        MICRONAUT_CONFIG = new HashMap<>();
        MICRONAUT_CONFIG.put("micronaut.jimmer.language", "java");
        MICRONAUT_CONFIG.put(
                "micronaut.jimmer.dialect", "org.babyfish.jimmer.sql.dialect.H2Dialect");
        MICRONAUT_CONFIG.put("micronaut.jimmer.showSql", false);
        MICRONAUT_CONFIG.put("micronaut.jimmer.prettySql", false);
        MICRONAUT_CONFIG.put("micronaut.jimmer.inlineSqlVariables", false);

        MICRONAUT_CONFIG.put(
                "datasources.default.url",
                "jdbc:h2:mem:micronauttestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        MICRONAUT_CONFIG.put("datasources.default.driver-class-name", "org.h2.Driver");
        MICRONAUT_CONFIG.put("datasources.default.username", "sa");
        MICRONAUT_CONFIG.put("datasources.default.pool-name", "MicronautHikariPool");
        MICRONAUT_CONFIG.put("datasources.default.maximum-pool-size", 10);
        MICRONAUT_CONFIG.put("datasources.default.minimum-idle", 5);
        MICRONAUT_CONFIG.put("datasources.default.idle-timeout", 30000);
        MICRONAUT_CONFIG.put("datasources.default.max-lifetime", 1800000);
        MICRONAUT_CONFIG.put("datasources.default.connection-timeout", 30000);
        MICRONAUT_CONFIG.put("datasources.default.leak-detection-threshold", 0);
        MICRONAUT_CONFIG.put("datasources.default.validation-timeout", 5000);

        MICRONAUT_CONFIG.put("logger.levels.io.micronaut.context.env", "INFO");
    }

    @Param({"10", "20", "50", "100", "200", "500", "1000"})
    private int dataCount;

    private ApplicationContext micronautContext;

    private ConnectionOperations<Connection> connectionOperations;

    @Setup
    public void initialize() throws SQLException, IOException {
        micronautContext = ApplicationContext.run(MICRONAUT_CONFIG);
        connectionOperations = micronautContext.getBean(ConnectionOperations.class);
        micronautContext.registerSingleton(
                MicronautDatabaseInitializer.class,
                new MicronautDatabaseInitializer(connectionOperations));
        MicronautDatabaseInitializer micronautInitializer =
                micronautContext.getBean(MicronautDatabaseInitializer.class);
        micronautInitializer.initialize(dataCount);
    }

    @Benchmark
    public void testMicronaut() {
        JSqlClient sqlClient = micronautContext.getBean(JSqlClient.class);
        sqlClient
                .createQuery(Tables.JIMMER_DATA_MICRONAUT_TABLE)
                .select(JimmerDataMicronautTable.$)
                .execute();
    }

    @Benchmark
    public void testMicronautWithDatasource() {
        connectionOperations.execute(
                ConnectionDefinition.DEFAULT,
                status -> {
                    Connection connection = status.getConnection();
                    try (Statement statement = connection.createStatement()) {
                        return statement.execute("SELECT * FROM data_micronaut");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @TearDown
    public void tearDown() {
        if (micronautContext != null) {
            micronautContext.close();
        }
    }
}
