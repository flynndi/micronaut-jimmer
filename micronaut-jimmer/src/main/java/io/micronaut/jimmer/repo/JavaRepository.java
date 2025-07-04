package io.micronaut.jimmer.repo;

import java.util.List;
import java.util.Map;
import org.babyfish.jimmer.Input;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.Slice;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.meta.TypedProp;
import org.babyfish.jimmer.sql.ast.mutation.*;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface JavaRepository<E, ID> {

    /**
     * @param id id
     */
    @Nullable
    default E findById(ID id) {
        return findById(id, (Fetcher<E>) null);
    }

    /**
     * @param id id
     * @param fetcher fetcher
     */
    @Nullable
    E findById(ID id, @Nullable Fetcher<E> fetcher);

    /**
     * @param id id
     * @param viewType viewType
     * @param <V> V
     */
    @Nullable
    <V extends View<E>> V findById(ID id, Class<V> viewType);

    /**
     * @param ids ids
     */
    @NotNull
    default List<E> findByIds(Iterable<ID> ids) {
        return findByIds(ids, (Fetcher<E>) null);
    }

    /**
     * @param ids ids
     * @param fetcher fetcher
     */
    @NotNull
    List<E> findByIds(Iterable<ID> ids, @Nullable Fetcher<E> fetcher);

    /**
     * @param ids ids
     * @param viewType viewType
     * @param <V> V
     */
    @NotNull
    <V extends View<E>> List<V> findByIds(Iterable<ID> ids, Class<V> viewType);

    /**
     * @param ids ids
     */
    @NotNull
    default Map<ID, E> findMapByIds(Iterable<ID> ids) {
        return findMapByIds(ids, (Fetcher<E>) null);
    }

    /**
     * @param ids ids
     * @param fetcher fetcher
     */
    @NotNull
    Map<ID, E> findMapByIds(Iterable<ID> ids, Fetcher<E> fetcher);

    /**
     * @param ids ids
     * @param viewType viewType
     * @param <V> V
     */
    @NotNull
    <V extends View<E>> Map<ID, V> findMapByIds(Iterable<ID> ids, Class<V> viewType);

    /**
     * @param sortedProps sortedProps
     */
    @NotNull
    default List<E> findAll(TypedProp.Scalar<?, ?>... sortedProps) {
        return findAll((Fetcher<E>) null, sortedProps);
    }

    /**
     * @param fetcher fetcher
     * @param sortedProps sortedProps
     */
    @NotNull
    List<E> findAll(@Nullable Fetcher<E> fetcher, TypedProp.Scalar<?, ?>... sortedProps);

    /**
     * @param viewType viewType
     * @param sortedProps sortedProps
     * @param <V> V
     */
    @NotNull
    <V extends View<E>> List<V> findAll(Class<V> viewType, TypedProp.Scalar<?, ?>... sortedProps);

    /**
     * @param pageParam pageParam
     * @param sortedProps sortedProps
     */
    @NotNull
    default Page<E> findPage(PageParam pageParam, TypedProp.Scalar<?, ?>... sortedProps) {
        return findPage(pageParam, (Fetcher<E>) null, sortedProps);
    }

    /**
     * @param pageParam pageParam
     * @param fetcher fetcher
     * @param sortedProps sortedProps
     */
    @NotNull
    Page<E> findPage(
            PageParam pageParam,
            @Nullable Fetcher<E> fetcher,
            TypedProp.Scalar<?, ?>... sortedProps);

    /**
     * @param pageParam pageParam
     * @param viewType viewType
     * @param sortedProps sortedProps
     * @param <V> V
     */
    @NotNull
    <V extends View<E>> Page<V> findPage(
            PageParam pageParam, Class<V> viewType, TypedProp.Scalar<?, ?>... sortedProps);

    /**
     * @param limit limit
     * @param offset offset
     * @param sortedProps sortedProps
     */
    @NotNull
    default Slice<E> findSlice(int limit, int offset, TypedProp.Scalar<?, ?>... sortedProps) {
        return findSlice(limit, offset, (Fetcher<E>) null, sortedProps);
    }

    /**
     * @param limit limit
     * @param offset offset
     * @param fetcher fetcher
     * @param sortedProps sortedProps
     */
    @NotNull
    Slice<E> findSlice(
            int limit,
            int offset,
            @Nullable Fetcher<E> fetcher,
            TypedProp.Scalar<?, ?>... sortedProps);

    /**
     * @param limit limit
     * @param offset offset
     * @param viewType viewType
     * @param sortedProps sortedProps
     * @param <V> V
     */
    @NotNull
    <V extends View<E>> Slice<V> findSlice(
            int limit, int offset, Class<V> viewType, TypedProp.Scalar<?, ?>... sortedProps);

    /**
     * @param entity entity
     */
    @NotNull
    SimpleEntitySaveCommand<E> saveCommand(@NotNull E entity);

    /**
     * @param input input
     */
    @NotNull
    default SimpleEntitySaveCommand<E> saveCommand(@NotNull Input<E> input) {
        return saveCommand(input.toEntity());
    }

    /**
     * @param entities entities
     */
    @NotNull
    BatchEntitySaveCommand<E> saveEntitiesCommand(@NotNull Iterable<E> entities);

    /**
     * @param inputs inputs
     */
    @NotNull
    BatchEntitySaveCommand<E> saveInputsCommand(@NotNull Iterable<? extends Input<E>> inputs);

    /**
     * @param entity entity
     */
    default SimpleSaveResult<E> save(E entity) {
        return saveCommand(entity).execute();
    }

    /**
     * @param entity entity
     * @param mode mode
     */
    default SimpleSaveResult<E> save(E entity, SaveMode mode) {
        return saveCommand(entity).setMode(mode).execute();
    }

    /**
     * @param entity entity
     * @param mode mode
     * @param associatedMode associatedMode
     */
    default SimpleSaveResult<E> save(E entity, SaveMode mode, AssociatedSaveMode associatedMode) {
        return saveCommand(entity).setMode(mode).setAssociatedModeAll(associatedMode).execute();
    }

    /**
     * @param entities entities
     */
    default BatchSaveResult<E> saveEntities(Iterable<E> entities) {
        return saveEntitiesCommand(entities).execute();
    }

    /**
     * @param entities entities
     * @param mode mode
     */
    default BatchSaveResult<E> saveEntities(Iterable<E> entities, SaveMode mode) {
        return saveEntitiesCommand(entities).setMode(mode).execute();
    }

    /**
     * @param entities entities
     * @param mode mode
     * @param associatedMode associatedMode
     */
    default BatchSaveResult<E> saveEntities(
            Iterable<E> entities, SaveMode mode, AssociatedSaveMode associatedMode) {
        return saveEntitiesCommand(entities)
                .setMode(mode)
                .setAssociatedModeAll(associatedMode)
                .execute();
    }

    /**
     * @param input input
     */
    default SimpleSaveResult<E> save(Input<E> input) {
        return saveCommand(input).execute();
    }

    /**
     * @param input input
     * @param mode mode
     */
    default SimpleSaveResult<E> save(Input<E> input, SaveMode mode) {
        return saveCommand(input).setMode(mode).execute();
    }

    /**
     * @param input input
     * @param mode mode
     * @param associatedMode associatedMode
     */
    default SimpleSaveResult<E> save(
            Input<E> input, SaveMode mode, AssociatedSaveMode associatedMode) {
        return saveCommand(input).setMode(mode).setAssociatedModeAll(associatedMode).execute();
    }

    /**
     * @param inputs inputs
     */
    default BatchSaveResult<E> saveInputs(Iterable<? extends Input<E>> inputs) {
        return saveInputsCommand(inputs).execute();
    }

    /**
     * @param inputs inputs
     * @param mode mode
     */
    default BatchSaveResult<E> saveInputs(Iterable<? extends Input<E>> inputs, SaveMode mode) {
        return saveInputsCommand(inputs).setMode(mode).execute();
    }

    /**
     * @param inputs inputs
     * @param mode mode
     * @param associatedMode associatedMode
     */
    default BatchSaveResult<E> saveInputs(
            Iterable<? extends Input<E>> inputs, SaveMode mode, AssociatedSaveMode associatedMode) {
        return saveInputsCommand(inputs)
                .setMode(mode)
                .setAssociatedModeAll(associatedMode)
                .execute();
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param entity entity
     * @param fetcher fetcher
     */
    @Deprecated
    default SimpleSaveResult<E> save(E entity, Fetcher<E> fetcher) {
        return saveCommand(entity).execute(fetcher);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param entity entity
     * @param mode mode
     * @param associatedMode associatedMode
     * @param fetcher fetcher
     */
    @Deprecated
    default SimpleSaveResult<E> save(
            E entity, SaveMode mode, AssociatedSaveMode associatedMode, Fetcher<E> fetcher) {
        return saveCommand(entity)
                .setMode(mode)
                .setAssociatedModeAll(associatedMode)
                .execute(fetcher);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param entities entities
     * @param fetcher fetcher
     */
    @Deprecated
    default BatchSaveResult<E> saveEntities(Iterable<E> entities, Fetcher<E> fetcher) {
        return saveEntitiesCommand(entities).execute(fetcher);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param entities entities
     * @param mode mode
     * @param associatedMode associatedMode
     * @param fetcher fetcher
     */
    @Deprecated
    default BatchSaveResult<E> saveEntities(
            Iterable<E> entities,
            SaveMode mode,
            AssociatedSaveMode associatedMode,
            Fetcher<E> fetcher) {
        return saveEntitiesCommand(entities)
                .setMode(mode)
                .setAssociatedModeAll(associatedMode)
                .execute(fetcher);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param input input
     * @param fetcher fetcher
     */
    @Deprecated
    default SimpleSaveResult<E> save(Input<E> input, Fetcher<E> fetcher) {
        return saveCommand(input).execute(fetcher);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param input input
     * @param mode mode
     * @param associatedMode associatedMode
     * @param fetcher fetcher
     */
    @Deprecated
    default SimpleSaveResult<E> save(
            Input<E> input, SaveMode mode, AssociatedSaveMode associatedMode, Fetcher<E> fetcher) {
        return saveCommand(input)
                .setMode(mode)
                .setAssociatedModeAll(associatedMode)
                .execute(fetcher);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param inputs inputs
     * @param fetcher fetcher
     */
    @Deprecated
    default BatchSaveResult<E> saveInputs(Iterable<? extends Input<E>> inputs, Fetcher<E> fetcher) {
        return saveInputsCommand(inputs).execute(fetcher);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param inputs inputs
     * @param mode mode
     * @param associatedMode associatedMode
     * @param fetcher fetcher
     */
    @Deprecated
    default BatchSaveResult<E> saveInputs(
            Iterable<? extends Input<E>> inputs,
            SaveMode mode,
            AssociatedSaveMode associatedMode,
            Fetcher<E> fetcher) {
        return saveInputsCommand(inputs)
                .setMode(mode)
                .setAssociatedModeAll(associatedMode)
                .execute(fetcher);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param entity entity
     * @param viewType viewType
     */
    @Deprecated
    default <V extends View<E>> SimpleSaveResult.View<E, V> save(E entity, Class<V> viewType) {
        return saveCommand(entity).execute(viewType);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param entity entity
     * @param mode mode
     * @param associatedMode associatedMode
     * @param viewType viewType
     */
    @Deprecated
    default <V extends View<E>> SimpleSaveResult.View<E, V> save(
            E entity, SaveMode mode, AssociatedSaveMode associatedMode, Class<V> viewType) {
        return saveCommand(entity)
                .setMode(mode)
                .setAssociatedModeAll(associatedMode)
                .execute(viewType);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param entities entities
     * @param viewType viewType
     */
    @Deprecated
    default <V extends View<E>> BatchSaveResult.View<E, V> saveEntities(
            Iterable<E> entities, Class<V> viewType) {
        return saveEntitiesCommand(entities).execute(viewType);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param entities entities
     * @param mode mode
     * @param associatedMode associatedMode
     * @param viewType viewType
     */
    @Deprecated
    default <V extends View<E>> BatchSaveResult.View<E, V> saveEntities(
            Iterable<E> entities,
            SaveMode mode,
            AssociatedSaveMode associatedMode,
            Class<V> viewType) {
        return saveEntitiesCommand(entities)
                .setMode(mode)
                .setAssociatedModeAll(associatedMode)
                .execute(viewType);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param input input
     * @param viewType viewType
     */
    @Deprecated
    default <V extends View<E>> SimpleSaveResult.View<E, V> save(
            Input<E> input, Class<V> viewType) {
        return saveCommand(input).execute(viewType);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param input input
     * @param mode mode
     * @param associatedMode associatedMode
     * @param viewType viewType
     */
    @Deprecated
    default <V extends View<E>> SimpleSaveResult.View<E, V> save(
            Input<E> input, SaveMode mode, AssociatedSaveMode associatedMode, Class<V> viewType) {
        return saveCommand(input)
                .setMode(mode)
                .setAssociatedModeAll(associatedMode)
                .execute(viewType);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param inputs inputs
     * @param viewType viewType
     */
    @Deprecated
    default <V extends View<E>> BatchSaveResult.View<E, V> saveInputs(
            Iterable<? extends Input<E>> inputs, Class<V> viewType) {
        return saveInputsCommand(inputs).execute(viewType);
    }

    /**
     * @deprecated Saving and re-fetching by fetcher/viewer is advanced feature, please use
     *     `saveCommand`
     * @param inputs inputs
     * @param mode mode
     * @param associatedMode associatedMode
     * @param viewType viewType
     */
    @Deprecated
    default <V extends View<E>> BatchSaveResult.View<E, V> saveInputs(
            Iterable<? extends Input<E>> inputs,
            SaveMode mode,
            AssociatedSaveMode associatedMode,
            Class<V> viewType) {
        return saveInputsCommand(inputs)
                .setMode(mode)
                .setAssociatedModeAll(associatedMode)
                .execute(viewType);
    }

    /**
     * @param entity entity
     * @param associatedMode associatedMode
     */
    @Deprecated
    default SimpleSaveResult<E> save(E entity, AssociatedSaveMode associatedMode) {
        return saveCommand(entity).setAssociatedModeAll(associatedMode).execute();
    }

    /**
     * @param entities entities
     * @param associatedMode associatedMode
     */
    @Deprecated
    default BatchSaveResult<E> saveEntities(
            Iterable<E> entities, AssociatedSaveMode associatedMode) {
        return saveEntitiesCommand(entities).setAssociatedModeAll(associatedMode).execute();
    }

    /**
     * @param input input
     * @param associatedMode associatedMode
     */
    @Deprecated
    default SimpleSaveResult<E> save(Input<E> input, AssociatedSaveMode associatedMode) {
        return saveCommand(input).setAssociatedModeAll(associatedMode).execute();
    }

    /**
     * @param inputs inputs
     * @param associatedMode associatedMode
     */
    @Deprecated
    default BatchSaveResult<E> saveInputs(
            Iterable<? extends Input<E>> inputs, AssociatedSaveMode associatedMode) {
        return saveInputsCommand(inputs).setAssociatedModeAll(associatedMode).execute();
    }

    /**
     * @param entity entity
     * @param associatedMode associatedMode
     * @param fetcher fetcher
     */
    @Deprecated
    default SimpleSaveResult<E> save(
            E entity, AssociatedSaveMode associatedMode, Fetcher<E> fetcher) {
        return saveCommand(entity).setAssociatedModeAll(associatedMode).execute(fetcher);
    }

    /**
     * @param entity entity
     * @param mode mode
     * @param fetcher fetcher
     */
    @Deprecated
    default SimpleSaveResult<E> save(E entity, SaveMode mode, Fetcher<E> fetcher) {
        return saveCommand(entity).setMode(mode).execute(fetcher);
    }

    /**
     * @param entities entities
     * @param associatedMode associatedMode
     * @param fetcher fetcher
     */
    @Deprecated
    default BatchSaveResult<E> saveEntities(
            Iterable<E> entities, AssociatedSaveMode associatedMode, Fetcher<E> fetcher) {
        return saveEntitiesCommand(entities).setAssociatedModeAll(associatedMode).execute(fetcher);
    }

    /**
     * @param entities entities
     * @param mode mode
     * @param fetcher fetcher
     */
    @Deprecated
    default BatchSaveResult<E> saveEntities(
            Iterable<E> entities, SaveMode mode, Fetcher<E> fetcher) {
        return saveEntitiesCommand(entities).setMode(mode).execute(fetcher);
    }

    /**
     * @param input input
     * @param associatedMode associatedMode
     * @param fetcher fetcher
     */
    @Deprecated
    default SimpleSaveResult<E> save(
            Input<E> input, AssociatedSaveMode associatedMode, Fetcher<E> fetcher) {
        return saveCommand(input).setAssociatedModeAll(associatedMode).execute(fetcher);
    }

    /**
     * @param input input
     * @param mode mode
     * @param fetcher fetcher
     */
    @Deprecated
    default SimpleSaveResult<E> save(Input<E> input, SaveMode mode, Fetcher<E> fetcher) {
        return saveCommand(input).setMode(mode).execute(fetcher);
    }

    /**
     * @param inputs inputs
     * @param associatedMode associatedMode
     * @param fetcher fetcher
     */
    @Deprecated
    default BatchSaveResult<E> saveInputs(
            Iterable<? extends Input<E>> inputs,
            AssociatedSaveMode associatedMode,
            Fetcher<E> fetcher) {
        return saveInputsCommand(inputs).setAssociatedModeAll(associatedMode).execute(fetcher);
    }

    /**
     * @param inputs inputs
     * @param mode mode
     * @param fetcher fetcher
     */
    @Deprecated
    default BatchSaveResult<E> saveInputs(
            Iterable<? extends Input<E>> inputs, SaveMode mode, Fetcher<E> fetcher) {
        return saveInputsCommand(inputs).setMode(mode).execute(fetcher);
    }

    /**
     * @param entity entity
     * @param associatedMode associatedMode
     * @param viewType viewType
     * @param <V> V
     */
    @Deprecated
    default <V extends View<E>> SimpleSaveResult.View<E, V> save(
            E entity, AssociatedSaveMode associatedMode, Class<V> viewType) {
        return saveCommand(entity).setAssociatedModeAll(associatedMode).execute(viewType);
    }

    /**
     * @param entity entity
     * @param mode mode
     * @param viewType viewType
     * @param <V> V
     */
    @Deprecated
    default <V extends View<E>> SimpleSaveResult.View<E, V> save(
            E entity, SaveMode mode, Class<V> viewType) {
        return saveCommand(entity).setMode(mode).execute(viewType);
    }

    /**
     * @param entities entities
     * @param associatedMode associatedMode
     * @param viewType viewType
     * @param <V> V
     */
    @Deprecated
    default <V extends View<E>> BatchSaveResult.View<E, V> saveEntities(
            Iterable<E> entities, AssociatedSaveMode associatedMode, Class<V> viewType) {
        return saveEntitiesCommand(entities).setAssociatedModeAll(associatedMode).execute(viewType);
    }

    /**
     * @param entities entities
     * @param mode mode
     * @param viewType viewType
     * @param <V> V
     */
    @Deprecated
    default <V extends View<E>> BatchSaveResult.View<E, V> saveEntities(
            Iterable<E> entities, SaveMode mode, Class<V> viewType) {
        return saveEntitiesCommand(entities).setMode(mode).execute(viewType);
    }

    /**
     * @param input input
     * @param associatedMode associatedMode
     * @param viewType viewType
     * @param <V> V
     */
    @Deprecated
    default <V extends View<E>> SimpleSaveResult.View<E, V> save(
            Input<E> input, AssociatedSaveMode associatedMode, Class<V> viewType) {
        return saveCommand(input).setAssociatedModeAll(associatedMode).execute(viewType);
    }

    /**
     * @param input input
     * @param mode mode
     * @param viewType viewType
     * @param <V> V
     */
    @Deprecated
    default <V extends View<E>> SimpleSaveResult.View<E, V> save(
            Input<E> input, SaveMode mode, Class<V> viewType) {
        return saveCommand(input).setMode(mode).execute(viewType);
    }

    /**
     * @param inputs inputs
     * @param associatedMode associatedMode
     * @param viewType viewType
     * @param <V> V
     */
    @Deprecated
    default <V extends View<E>> BatchSaveResult.View<E, V> saveInputs(
            Iterable<? extends Input<E>> inputs,
            AssociatedSaveMode associatedMode,
            Class<V> viewType) {
        return saveInputsCommand(inputs).setAssociatedModeAll(associatedMode).execute(viewType);
    }

    /**
     * @param inputs inputs
     * @param mode mode
     * @param viewType viewType
     * @param <V> V
     */
    @Deprecated
    default <V extends View<E>> BatchSaveResult.View<E, V> saveInputs(
            Iterable<? extends Input<E>> inputs, SaveMode mode, Class<V> viewType) {
        return saveInputsCommand(inputs).setMode(mode).execute(viewType);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#insert(Object)}, please view that
     * method to know more
     *
     * @param entity entity
     */
    @Deprecated
    default SimpleSaveResult<E> insert(E entity) {
        return save(entity, SaveMode.INSERT_ONLY, AssociatedSaveMode.APPEND);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#insert(Object, AssociatedSaveMode)},
     * please view that method to know more
     *
     * @param entity entity
     * @param associatedSaveMode associatedSaveMode
     */
    @Deprecated
    default SimpleSaveResult<E> insert(E entity, AssociatedSaveMode associatedSaveMode) {
        return save(entity, SaveMode.INSERT_ONLY, associatedSaveMode);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#insert(Input)}, please view that
     * method to know more
     *
     * @param input input
     */
    @Deprecated
    default SimpleSaveResult<E> insert(Input<E> input) {
        return save(input, SaveMode.INSERT_ONLY, AssociatedSaveMode.APPEND);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#insert(Input, AssociatedSaveMode)},
     * please view that method to know more
     *
     * @param input input
     * @param associatedSaveMode associatedSaveMode
     */
    @Deprecated
    default SimpleSaveResult<E> insert(Input<E> input, AssociatedSaveMode associatedSaveMode) {
        return save(input, SaveMode.INSERT_ONLY, associatedSaveMode);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#insertIfAbsent(Object)}, please view
     * that method to know more
     *
     * @param entity entity
     */
    @Deprecated
    default SimpleSaveResult<E> insertIfAbsent(E entity) {
        return save(entity, SaveMode.INSERT_IF_ABSENT, AssociatedSaveMode.APPEND_IF_ABSENT);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#insertIfAbsent(Object,
     * AssociatedSaveMode)}, please view that method to know more
     *
     * @param entity entity
     * @param associatedSaveMode associatedSaveMode
     */
    @Deprecated
    default SimpleSaveResult<E> insertIfAbsent(E entity, AssociatedSaveMode associatedSaveMode) {
        return save(entity, SaveMode.INSERT_IF_ABSENT, associatedSaveMode);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#insertIfAbsent(Input)}, please view
     * that method to know more
     *
     * @param input input
     */
    @Deprecated
    default SimpleSaveResult<E> insertIfAbsent(Input<E> input) {
        return save(input, SaveMode.INSERT_IF_ABSENT, AssociatedSaveMode.APPEND_IF_ABSENT);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#insertIfAbsent(Input,
     * AssociatedSaveMode)}, please view that method to know more
     *
     * @param input input
     * @param associatedSaveMode associatedSaveMode
     */
    @Deprecated
    default SimpleSaveResult<E> insertIfAbsent(
            Input<E> input, AssociatedSaveMode associatedSaveMode) {
        return save(input, SaveMode.INSERT_IF_ABSENT, associatedSaveMode);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#update(Object)}, please view that
     * method to know more
     *
     * @param entity entity
     */
    @Deprecated
    default SimpleSaveResult<E> update(E entity) {
        return save(entity, SaveMode.UPDATE_ONLY, AssociatedSaveMode.UPDATE);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#update(Object, AssociatedSaveMode)},
     * please view that method to know more
     *
     * @param entity entity
     * @param associatedSaveMode associatedSaveMode
     */
    @Deprecated
    default SimpleSaveResult<E> update(E entity, AssociatedSaveMode associatedSaveMode) {
        return save(entity, SaveMode.UPDATE_ONLY, associatedSaveMode);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#update(Input)}, please view that
     * method to know more
     *
     * @param input input
     */
    @Deprecated
    default SimpleSaveResult<E> update(Input<E> input) {
        return save(input, SaveMode.UPDATE_ONLY, AssociatedSaveMode.UPDATE);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#update(Input, AssociatedSaveMode)},
     * please view that method to know more
     *
     * @param input input
     * @param associatedSaveMode associatedSaveMode
     */
    @Deprecated
    default SimpleSaveResult<E> update(Input<E> input, AssociatedSaveMode associatedSaveMode) {
        return save(input, SaveMode.UPDATE_ONLY, associatedSaveMode);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#merge(Object)}, please view that
     * method to know more
     *
     * @param entity entity
     */
    @Deprecated
    default SimpleSaveResult<E> merge(E entity) {
        return save(entity, SaveMode.UPSERT, AssociatedSaveMode.MERGE);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#merge(Object)}, please view that
     * method to know more
     *
     * @param entity entity
     * @param associatedSaveMode associatedSaveMode
     */
    @Deprecated
    default SimpleSaveResult<E> merge(E entity, AssociatedSaveMode associatedSaveMode) {
        return save(entity, SaveMode.UPSERT, associatedSaveMode);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#merge(Input)}, please view that method
     * to know more
     *
     * @param input input
     */
    @Deprecated
    default SimpleSaveResult<E> merge(Input<E> input) {
        return save(input, SaveMode.UPSERT, AssociatedSaveMode.MERGE);
    }

    /**
     * Shortcut for {@link org.babyfish.jimmer.sql.JSqlClient#merge(Input)}, please view that method
     * to know more
     *
     * @param input input
     * @param associatedSaveMode associatedSaveMode
     */
    @Deprecated
    default SimpleSaveResult<E> merge(Input<E> input, AssociatedSaveMode associatedSaveMode) {
        return save(input, SaveMode.UPSERT, associatedSaveMode);
    }

    /**
     * @param id id
     */
    default long deleteById(ID id) {
        return deleteById(id, DeleteMode.AUTO);
    }

    /**
     * @param id id
     * @param deleteMode deleteMode
     */
    long deleteById(ID id, DeleteMode deleteMode);

    /**
     * @param ids ids
     */
    default long deleteByIds(Iterable<ID> ids) {
        return deleteByIds(ids, DeleteMode.AUTO);
    }

    /**
     * @param ids ids
     * @param deleteMode deleteMode
     */
    long deleteByIds(Iterable<ID> ids, DeleteMode deleteMode);
}
