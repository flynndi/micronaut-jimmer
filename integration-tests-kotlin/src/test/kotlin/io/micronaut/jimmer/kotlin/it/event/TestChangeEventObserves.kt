package io.micronaut.jimmer.kotlin.it.event

import io.micronaut.jimmer.kotlin.it.entity.Book
import io.micronaut.jimmer.kotlin.it.entity.BookProps
import io.micronaut.jimmer.kotlin.it.entity.BookStoreProps
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.inject.Singleton
import org.babyfish.jimmer.sql.event.AssociationEvent
import org.babyfish.jimmer.sql.event.EntityEvent
import java.util.concurrent.CopyOnWriteArrayList

@Singleton
class TestChangeEventObserves {
    private val entityEventStorage: MutableList<EntityEvent<*>> = CopyOnWriteArrayList()

    private val associationEventStorageOne: MutableList<AssociationEvent> = CopyOnWriteArrayList()

    private val associationEventStorageTwo: MutableList<AssociationEvent> = CopyOnWriteArrayList()

    @EventListener
    fun entityChangeEvent(entityEvent: EntityEvent<*>) {
        if (entityEvent.getImmutableType().getJavaClass() == Book::class.java) {
            entityEventStorage.add(entityEvent)
        }
    }

    @EventListener
    fun associationChangeEvent(associationEvent: AssociationEvent) {
        when {
            associationEvent.isChanged(BookProps.STORE) -> associationEventStorageOne.add(associationEvent)
            associationEvent.isChanged(BookStoreProps.BOOKS) -> associationEventStorageTwo.add(associationEvent)
        }
    }

    fun getEntityEventStorage(): MutableList<EntityEvent<*>> = entityEventStorage

    fun getAssociationEventStorageOne(): MutableList<AssociationEvent> = associationEventStorageOne

    fun getAssociationEventStorageTwo(): MutableList<AssociationEvent> = associationEventStorageTwo
}
