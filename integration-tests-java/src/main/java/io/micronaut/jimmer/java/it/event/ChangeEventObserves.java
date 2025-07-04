package io.micronaut.jimmer.java.it.event;

import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.java.it.entity.BookProps;
import io.micronaut.jimmer.java.it.entity.BookStoreProps;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import org.babyfish.jimmer.sql.event.AssociationEvent;
import org.babyfish.jimmer.sql.event.EntityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ChangeEventObserves {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeEventObserves.class);

    @EventListener
    public void entityChangeEvent(EntityEvent<Book> entityEvent) {
        if (entityEvent.getImmutableType().getJavaClass() == Book.class) {
            LOGGER.info(
                    "The object `Book` is changed \told: {}, \tnew: {}",
                    entityEvent.getOldEntity(),
                    entityEvent.getNewEntity());
        }
    }

    @EventListener
    public void associationChangeEvent(AssociationEvent associationEvent) {
        if (associationEvent.isChanged(BookProps.STORE)) {
            LOGGER.info(
                    "The many-to-one association `Book.store` is changed, \tbook id: {}, \tdetached book store id: {}, \tattached book store id: {}",
                    associationEvent.getSourceId(),
                    associationEvent.getDetachedTargetId(),
                    associationEvent.getAttachedTargetId());
        } else if (associationEvent.isChanged(BookStoreProps.BOOKS)) {
            LOGGER.info(
                    "The one-to-many association `BookStore.books` is changed, \tbook store id: {}, \tdetached book id: {}, \tattached book id: {}",
                    associationEvent.getSourceId(),
                    associationEvent.getDetachedTargetId(),
                    associationEvent.getAttachedTargetId());
        }
    }
}
