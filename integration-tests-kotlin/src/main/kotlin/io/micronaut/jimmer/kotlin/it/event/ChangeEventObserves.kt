package io.micronaut.jimmer.kotlin.it.event

import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.kotlin.it.entity.BookProps
import io.micronaut.jimmer.kotlin.it.entity.BookStoreProps
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.inject.Singleton
import org.babyfish.jimmer.sql.event.AssociationEvent
import org.babyfish.jimmer.sql.event.EntityEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class ChangeEventObserves {
    private val logger: Logger = LoggerFactory.getLogger(ChangeEventObserves::class.java)

    @EventListener
    fun entityChangeEvent(entityEvent: EntityEvent<Book>) {
        if (entityEvent.getImmutableType().javaClass == Book::class.java) {
            logger.info(
                "The object `Book` is changed \told: {}, \tnew: {}",
                entityEvent.getOldEntity(),
                entityEvent.getNewEntity(),
            )
        }
    }

    @EventListener
    fun associationChangeEvent(associationEvent: AssociationEvent) {
        if (associationEvent.isChanged(BookProps.STORE)) {
            logger.info(
                "The many-to-one association `Book.store` is changed, \tbook id: {}, \tdetached book store id: {}, \tattached book store id: {}",
                associationEvent.getSourceId(),
                associationEvent.getDetachedTargetId(),
                associationEvent.getAttachedTargetId(),
            )
        } else if (associationEvent.isChanged(BookStoreProps.BOOKS)) {
            logger.info(
                "The one-to-many association `BookStore.books` is changed, \tbook store id: {}, \tdetached book id: {}, \tattached book id: {}",
                associationEvent.getSourceId(),
                associationEvent.getDetachedTargetId(),
                associationEvent.getAttachedTargetId(),
            )
        }
    }
}
