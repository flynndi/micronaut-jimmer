package io.micronaut.jimmer.java.it.event;

import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.java.it.entity.BookProps;
import io.micronaut.jimmer.java.it.entity.BookStoreProps;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.babyfish.jimmer.sql.event.AssociationEvent;
import org.babyfish.jimmer.sql.event.EntityEvent;

@Singleton
public class TestChangeEventObserves {

    private final List<EntityEvent<?>> entityEventStorage = new CopyOnWriteArrayList<>();

    private final List<AssociationEvent> associationEventStorageOne = new CopyOnWriteArrayList<>();

    private final List<AssociationEvent> associationEventStorageTwo = new CopyOnWriteArrayList<>();

    @EventListener
    public void entityChangeEvent(EntityEvent<?> entityEvent) {
        if (entityEvent.getImmutableType().getJavaClass() == Book.class) {
            entityEventStorage.add(entityEvent);
        }
    }

    @EventListener
    public void associationChangeEvent(AssociationEvent associationEvent) {
        if (associationEvent.isChanged(BookProps.STORE)) {
            associationEventStorageOne.add(associationEvent);
        } else if (associationEvent.isChanged(BookStoreProps.BOOKS)) {
            associationEventStorageTwo.add(associationEvent);
        }
    }

    public List<EntityEvent<?>> getEntityEventStorage() {
        return entityEventStorage;
    }

    public List<AssociationEvent> getAssociationEventStorageOne() {
        return associationEventStorageOne;
    }

    public List<AssociationEvent> getAssociationEventStorageTwo() {
        return associationEventStorageTwo;
    }
}
