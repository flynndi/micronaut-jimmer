package io.micronaut.jimmer.cache.impl;

import io.micronaut.context.annotation.*;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.order.Ordered;
import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Singleton;
import java.util.List;
import javax.sql.DataSource;
import org.babyfish.jimmer.sql.cache.TransactionCacheOperator;
import org.babyfish.jimmer.sql.event.DatabaseEvent;

@Singleton
@Internal
@EachBean(DataSource.class)
@Requires(condition = TriggerTypeNotBinlogCondition.class)
class TransactionCacheOperatorFlusher implements Ordered {

    private final List<TransactionCacheOperator> operators;

    private final ThreadLocal<Boolean> dirtyLocal = new ThreadLocal<>();

    public TransactionCacheOperatorFlusher(List<TransactionCacheOperator> operators) {
        if (operators.isEmpty()) {
            throw new IllegalArgumentException("`operators` cannot be empty");
        }
        this.operators = operators;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(DatabaseEvent e) {
        dirtyLocal.set(Boolean.TRUE);
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT)
    public void afterCommit(DatabaseEvent e) {
        if (dirtyLocal.get() != null) {
            dirtyLocal.remove();
            flush();
        }
    }

    @Scheduled(fixedRate = "${micronaut.jimmer.transactionCacheOperatorFixedDelay:5s}")
    public void retry() {
        flush();
    }

    private void flush() {
        if (operators.size() == 1) {
            TransactionCacheOperator operator = operators.get(0);
            operator.flush();
        } else {
            Throwable throwable = null;
            for (TransactionCacheOperator operator : operators) {
                try {
                    operator.flush();
                } catch (RuntimeException | Error ex) {
                    if (throwable == null) {
                        throwable = ex;
                    }
                }
            }
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            }
            if (throwable != null) {
                throw (Error) throwable;
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
