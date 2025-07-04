package io.framework.benchmark.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

@Component
public class SpringDatabaseInitializer {

    private final DataSource dataSource;

    public SpringDatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void initialize(int dataCount) throws SQLException, IOException {
        if (dataCount < 1) {
            throw new IllegalArgumentException("dataCount is less than 1");
        }
        Connection con = DataSourceUtils.getConnection(dataSource);
        try {
            create(con);
            insert(con, dataCount);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private void create(Connection con) throws SQLException {
        String sql =
                "drop table if exists data_springboot;\n"
                        + "create table data_springboot(\n"
                        + "    id bigint not null primary key,\n"
                        + "    value_1 int not null,\n"
                        + "    value_2 int not null,\n"
                        + "    value_3 int not null,\n"
                        + "    value_4 int not null,\n"
                        + "    value_5 int not null,\n"
                        + "    value_6 int not null,\n"
                        + "    value_7 int not null,\n"
                        + "    value_8 int not null,\n"
                        + "    value_9 int not null\n"
                        + ");";
        try (Statement stmt = con.createStatement()) {
            for (String s : sql.split(";")) {
                if (!s.trim().isEmpty()) {
                    stmt.execute(s);
                }
            }
        }
    }

    private void insert(Connection con, int dataCount) throws SQLException {
        String sql =
                "insert into data_springboot(id, "
                        + IntStream.range(1, 10)
                                .mapToObj(i -> "value_" + i)
                                .collect(Collectors.joining(", "))
                        + ") values("
                        + IntStream.range(0, 10)
                                .mapToObj(i -> "?")
                                .collect(Collectors.joining(", "))
                        + ")";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int row = 1; row <= dataCount; row++) {
                stmt.setLong(1, row);
                for (int col = 2; col <= 10; col++) {
                    stmt.setInt(col, ThreadLocalRandom.current().nextInt(100));
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}
