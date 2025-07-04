package io.micronaut.jimmer.cfg.support;

import io.micronaut.data.connection.ConnectionOperations;
import java.sql.Connection;
import org.babyfish.jimmer.sql.runtime.ConnectionManager;
import org.jetbrains.annotations.NotNull;

public interface DataSourceAwareConnectionManager extends ConnectionManager {

    @NotNull
    ConnectionOperations<Connection> getConnectionOperations();
}
