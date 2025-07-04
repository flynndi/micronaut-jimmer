package io.micronaut.jimmer.java.it.resource;

import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.java.it.entity.Immutables;
import io.micronaut.jimmer.java.it.service.IBook;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.transaction.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class TestTransactionTestCase {

    @Inject IBook iBook;

    @Inject TransactionManager transactionManager;

    @Test
    @Transactional
    public void testUserTransaction() throws Exception {
        Assertions.assertEquals(Status.STATUS_ACTIVE, transactionManager.getStatus());
    }

    @Test
    @Transactional
    public void testTransactionRollBack() {
        int id = 23;
        Book book =
                Immutables.createBook(
                        draft -> {
                            draft.setId(id);
                            draft.setName("Transactional");
                            draft.setPrice(new BigDecimal("1"));
                            draft.setEdition(1);
                            draft.setStoreId(2L);
                            draft.setTenant("test");
                            draft.setCreatedTime(LocalDateTime.now());
                            draft.setModifiedTime(LocalDateTime.now());
                        });
        Assertions.assertThrows(ArithmeticException.class, () -> iBook.save(book));
    }

    @Test
    public void testTransactionIsRollBack() throws SystemException, NotSupportedException {
        int id = 23;
        Book book =
                Immutables.createBook(
                        draft -> {
                            draft.setId(id);
                            draft.setName("Transactional");
                            draft.setPrice(new BigDecimal("1"));
                            draft.setEdition(1);
                            draft.setStoreId(2L);
                        });
        transactionManager.begin();
        try {
            iBook.save(book);
        } catch (Exception e) {
            transactionManager.rollback();
        }
        try {
            transactionManager.commit();
        } catch (Exception commitException) {
            // Handle commit exception if needed
        }
        Assertions.assertNull(iBook.findById(id));
    }
}
