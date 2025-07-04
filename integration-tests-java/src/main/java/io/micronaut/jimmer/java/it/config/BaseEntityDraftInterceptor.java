package io.micronaut.jimmer.java.it.config;

import io.micronaut.jimmer.java.it.entity.BaseEntity;
import io.micronaut.jimmer.java.it.entity.BaseEntityDraft;
import io.micronaut.jimmer.java.it.entity.BaseEntityProps;
import jakarta.inject.Singleton;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.meta.TypedProp;
import org.babyfish.jimmer.sql.DraftInterceptor;
import org.jetbrains.annotations.Nullable;

@Singleton
public class BaseEntityDraftInterceptor implements DraftInterceptor<BaseEntity, BaseEntityDraft> {

    @Override
    public void beforeSave(BaseEntityDraft draft, @Nullable BaseEntity original) {
        if (!ImmutableObjects.isLoaded(draft, BaseEntityProps.MODIFIED_TIME)) {
            draft.setModifiedTime(LocalDateTime.now());
        }
        if (original == null) {
            if (!ImmutableObjects.isLoaded(draft, BaseEntityProps.CREATED_TIME)) {
                draft.setCreatedTime(LocalDateTime.now());
            }
        }
    }

    @Nullable
    @Override
    public Collection<TypedProp<BaseEntity, ?>> dependencies() {
        return Arrays.asList(BaseEntityProps.CREATED_TIME, BaseEntityProps.MODIFIED_TIME);
    }
}
