package io.micronaut.jimmer.kotlin.it.config

import io.micronaut.jimmer.kotlin.it.entity.BaseEntity
import io.micronaut.jimmer.kotlin.it.entity.BaseEntityDraft
import io.micronaut.jimmer.kotlin.it.entity.BaseEntityProps
import jakarta.inject.Singleton
import org.babyfish.jimmer.ImmutableObjects
import org.babyfish.jimmer.meta.TypedProp
import org.babyfish.jimmer.sql.DraftInterceptor
import java.time.LocalDateTime

@Singleton
class BaseEntityDraftInterceptor : DraftInterceptor<BaseEntity, BaseEntityDraft> {
    override fun beforeSave(
        draft: BaseEntityDraft,
        original: BaseEntity?,
    ) {
        if (!ImmutableObjects.isLoaded(draft, BaseEntityProps.MODIFIED_TIME)) {
            draft.modifiedTime = LocalDateTime.now()
        }
        if (original == null) {
            if (!ImmutableObjects.isLoaded(draft, BaseEntityProps.CREATED_TIME)) {
                draft.createdTime = LocalDateTime.now()
            }
        }
    }

    override fun dependencies(): Collection<TypedProp<BaseEntity, *>> = listOf(BaseEntityProps.CREATED_TIME, BaseEntityProps.MODIFIED_TIME)
}
