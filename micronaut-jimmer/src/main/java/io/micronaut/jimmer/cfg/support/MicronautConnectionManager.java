package io.micronaut.jimmer.cfg.support;

import io.micronaut.data.connection.ConnectionDefinition;
import io.micronaut.data.connection.ConnectionOperations;
import io.micronaut.transaction.TransactionDefinition;
import io.micronaut.transaction.TransactionStatus;
import io.micronaut.transaction.jdbc.DataSourceTransactionManager;
import io.micronaut.transaction.support.DefaultTransactionDefinition;
import java.sql.Connection;
import java.util.function.Function;
import java.util.function.Supplier;
import org.babyfish.jimmer.sql.transaction.Propagation;
import org.babyfish.jimmer.sql.transaction.TxConnectionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MicronautConnectionManager
        implements DataSourceAwareConnectionManager, TxConnectionManager {

    private final ConnectionOperations<Connection> connectionOperations;

    private final Supplier<DataSourceTransactionManager> transactionManagerResolver;

    private volatile Object transactionManagerOrException;

    public MicronautConnectionManager(
            ConnectionOperations<Connection> connectionOperations,
            Supplier<DataSourceTransactionManager> transactionManagerResolver) {
        this.connectionOperations = connectionOperations;
        this.transactionManagerResolver = transactionManagerResolver;
    }

    @NotNull
    @Override
    public ConnectionOperations<Connection> getConnectionOperations() {
        return connectionOperations;
    }

    @Override
    public final <R> R execute(Function<Connection, R> block) {
        return execute(null, block);
    }

    @Override
    public <R> R execute(@Nullable Connection con, Function<Connection, R> block) {
        if (null != con) {
            return block.apply(con);
        }
        return connectionOperations.execute(
                ConnectionDefinition.DEFAULT, status -> block.apply(status.getConnection()));
    }

    @Override
    public final <R> R executeTransaction(Propagation propagation, Function<Connection, R> block) {
        DataSourceTransactionManager tm = transactionManager();
        TransactionStatus<Connection> ts =
                tm.getTransaction(new DefaultTransactionDefinition(behavior(propagation)));
        R result;
        try {
            result = execute(block);
        } catch (RuntimeException | Error ex) {
            tm.rollback(ts);
            throw ex;
        }
        tm.commit(ts);
        return result;
    }

    private DataSourceTransactionManager transactionManager() {
        Object obj = transactionManagerObject();
        if (obj instanceof RuntimeException) {
            throw (RuntimeException) obj;
        }
        if (obj instanceof Error) {
            throw (Error) obj;
        }
        return (DataSourceTransactionManager) obj;
    }

    private Object transactionManagerObject() {
        if (transactionManagerOrException == null) {
            synchronized (this) {
                if (transactionManagerOrException == null) {
                    if (transactionManagerResolver == null) {
                        transactionManagerOrException =
                                new IllegalStateException(
                                        "The current MicronautConnectionManager does not support "
                                                + "transaction management because its transactionManagerResolver "
                                                + "is not set");
                    } else {
                        try {
                            transactionManagerOrException = transactionManagerResolver.get();
                        } catch (RuntimeException | Error ex) {
                            transactionManagerOrException = ex;
                        }
                        if (transactionManagerOrException == null) {
                            transactionManagerOrException =
                                    new IllegalStateException(
                                            "The current MicronautConnectionManager does not support "
                                                    + "transaction management its transactionManagerResolver "
                                                    + "returns null");
                        }
                    }
                }
            }
        }
        return transactionManagerOrException;
    }

    private TransactionDefinition.Propagation behavior(Propagation propagation) {
        switch (propagation) {
            case REQUIRES_NEW:
                return TransactionDefinition.Propagation.REQUIRES_NEW;
            case SUPPORTS:
                return TransactionDefinition.Propagation.SUPPORTS;
            case NOT_SUPPORTED:
                return TransactionDefinition.Propagation.NOT_SUPPORTED;
            case MANDATORY:
                return TransactionDefinition.Propagation.MANDATORY;
            case NEVER:
                return TransactionDefinition.Propagation.NEVER;
            default:
                // REQUIRED:
                return TransactionDefinition.Propagation.REQUIRED;
        }
    }
}
