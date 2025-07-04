package io.micronaut.jimmer.cfg;

import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.language.*;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import io.micronaut.configuration.graphql.GraphQLExecutionInputCustomizer;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.meta.PropId;
import org.babyfish.jimmer.meta.TargetLevel;
import org.babyfish.jimmer.meta.impl.TypedPropImpl;
import org.babyfish.jimmer.runtime.ImmutableSpi;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.kt.KSqlClient;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;
import org.dataloader.MappedBatchLoader;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Singleton
@Requires(beans = GraphQL.class)
public class JimmerMicronautGraphQLConfiguration implements GraphQLExecutionInputCustomizer {

    private final JSqlClientImplementor sqlClient;

    public JimmerMicronautGraphQLConfiguration(
            Optional<JSqlClient> jSqlClientOptional, Optional<KSqlClient> kSqlClientOptional) {
        this.sqlClient = sqlClient(jSqlClientOptional, kSqlClientOptional);
    }

    @Override
    public Publisher<ExecutionInput> customize(
            ExecutionInput executionInput,
            HttpRequest httpRequest,
            @Nullable MutableHttpResponse<String> httpResponse) {
        DataLoaderRegistry registry = new DataLoaderRegistry();
        RuntimeWiring.Builder wiringBuilder = RuntimeWiring.newRuntimeWiring();
        for (ImmutableType type :
                sqlClient.getEntityManager().getAllTypes(sqlClient.getMicroServiceName())) {
            if (type.isEntity()) {
                TypeRuntimeWiring.Builder typeBuilder =
                        TypeRuntimeWiring.newTypeWiring(type.getJavaClass().getSimpleName());
                for (ImmutableProp prop : type.getProps().values()) {
                    if (prop.isReference(TargetLevel.ENTITY)) {
                        registry.register(
                                prop.toString(),
                                DataLoaderFactory.newMappedDataLoader(
                                        new ReferenceBatchLoader(prop, sqlClient)));
                        typeBuilder.dataFetcher(prop.getName(), new JimmerComplexFetcher(prop));
                    } else if (prop.isReferenceList(TargetLevel.ENTITY)) {
                        registry.register(
                                prop.toString(),
                                DataLoaderFactory.newMappedDataLoader(
                                        new ReferenceListBatchLoader(prop, sqlClient)));
                        typeBuilder.dataFetcher(prop.getName(), new JimmerComplexFetcher(prop));
                    } else if (prop.hasTransientResolver()) {
                        registry.register(
                                prop.toString(),
                                DataLoaderFactory.newMappedDataLoader(
                                        new TransientBatchLoader(prop, sqlClient)));
                        typeBuilder.dataFetcher(prop.getName(), new JimmerComplexFetcher(prop));
                    } else {
                        typeBuilder.dataFetcher(
                                prop.getName(), new JimmerSimpleFetcher(prop.getId()));
                    }
                }
                wiringBuilder.type(typeBuilder);
            }
        }
        executionInput.getDataLoaderRegistry().combine(registry);
        return Mono.just(executionInput.transform(builder -> builder.dataLoaderRegistry(registry)));
    }

    private static JSqlClientImplementor sqlClient(
            Optional<JSqlClient> jSqlClientOptional, Optional<KSqlClient> kSqlClientOptional) {
        if (jSqlClientOptional.isPresent()) {
            return (JSqlClientImplementor) jSqlClientOptional.get();
        } else if (kSqlClientOptional.isPresent()) {
            return kSqlClientOptional.get().getJavaClient();
        } else {
            throw new IllegalStateException("Neither JSqlClient nor KSqlClient is available");
        }
    }

    private static class ReferenceBatchLoader implements MappedBatchLoader<Object, Object> {

        private final ImmutableProp prop;
        private final JSqlClientImplementor sqlClient;

        private ReferenceBatchLoader(ImmutableProp prop, JSqlClientImplementor sqlClient) {
            this.prop = prop;
            this.sqlClient = sqlClient;
        }

        @Override
        public CompletionStage<Map<Object, Object>> load(Set<Object> keys) {
            return CompletableFuture.supplyAsync(
                    () ->
                            sqlClient
                                    .getLoaders()
                                    .reference(TypedPropImpl.Reference.of(prop))
                                    .batchLoad(keys));
        }
    }

    private static class ReferenceListBatchLoader implements MappedBatchLoader<Object, Object> {
        private final ImmutableProp prop;
        private final JSqlClientImplementor sqlClient;

        ReferenceListBatchLoader(ImmutableProp prop, JSqlClientImplementor sqlClient) {
            this.prop = prop;
            this.sqlClient = sqlClient;
        }

        @Override
        public CompletionStage<Map<Object, Object>> load(Set<Object> keys) {
            return CompletableFuture.supplyAsync(
                    () ->
                            (Map<Object, Object>)
                                    (Map<?, ?>)
                                            sqlClient
                                                    .getLoaders()
                                                    .list(new TypedPropImpl.ReferenceList<>(prop))
                                                    .batchLoad(keys));
        }
    }

    private static class TransientBatchLoader implements MappedBatchLoader<Object, Object> {
        private final ImmutableProp prop;
        private final JSqlClientImplementor sqlClient;

        TransientBatchLoader(ImmutableProp prop, JSqlClientImplementor sqlClient) {
            this.prop = prop;
            this.sqlClient = sqlClient;
        }

        @Override
        public CompletionStage<Map<Object, Object>> load(Set<Object> keys) {
            return CompletableFuture.supplyAsync(
                    () ->
                            sqlClient
                                    .getLoaders()
                                    .value(TypedPropImpl.Scalar.of(prop))
                                    .batchLoad(keys));
        }
    }

    private static class JimmerSimpleFetcher implements DataFetcher<Object> {

        private final PropId propId;

        JimmerSimpleFetcher(PropId propId) {
            this.propId = propId;
        }

        @Override
        public Object get(DataFetchingEnvironment env) {
            ImmutableSpi spi = env.getSource();
            return spi.__get(propId);
        }
    }

    private static class JimmerComplexFetcher implements DataFetcher<Object> {

        private final ImmutableProp prop;

        JimmerComplexFetcher(ImmutableProp prop) {
            this.prop = prop;
        }

        @Override
        public Object get(DataFetchingEnvironment env) {
            ImmutableSpi spi = env.getSource();
            if (spi.__isLoaded(prop.getId())) {
                Object value = spi.__get(prop.getId());
                if (value == null) {
                    return null;
                }
                if (!new UnloadedContext(env).isUnloaded(value)) {
                    return value;
                }
            }
            DataLoader<?, ?> dataLoader =
                    env.getDataLoaderRegistry().getDataLoader(prop.toString());
            if (dataLoader == null) {
                throw new IllegalStateException("No DataLoader for key '" + prop + "'");
            }
            return dataLoader.load(env.getSource());
        }
    }

    private static class UnloadedContext {

        private final DataFetchingEnvironment env;

        private UnloadedContext(DataFetchingEnvironment env) {
            this.env = env;
        }

        boolean isUnloaded(Object value) {
            SelectionSet selectionSet = env.getMergedField().getSingleField().getSelectionSet();
            if (value instanceof List<?>) {
                for (Object e : (List<?>) value) {
                    if (e instanceof ImmutableSpi && isUnloaded((ImmutableSpi) e, selectionSet)) {
                        return true;
                    }
                }
            } else if (value instanceof ImmutableSpi) {
                return isUnloaded((ImmutableSpi) value, selectionSet);
            }
            return false;
        }

        boolean isUnloaded(ImmutableSpi spi, SelectionSet selectionSet) {
            for (Selection<?> selection : selectionSet.getSelections()) {
                if (selection instanceof FragmentSpread) {
                    if (isUnloaded(spi, (FragmentSpread) selection)) {
                        return true;
                    }
                } else if (selection instanceof InlineFragment) {
                    if (isUnloaded(spi, (InlineFragment) selection)) {
                        return true;
                    }
                } else if (isUnloaded(spi, (Field) selection)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isUnloaded(ImmutableSpi spi, Field field) {
            if (field.getArguments() != null && !field.getArguments().isEmpty()) {
                return false;
            }
            ImmutableProp prop = spi.__type().getProps().get(field.getName());
            if (prop == null) {
                return false;
            }
            return !spi.__isLoaded(prop.getId());
        }

        private boolean isUnloaded(ImmutableSpi spi, FragmentSpread fragmentSpread) {
            FragmentDefinition definition = env.getFragmentsByName().get(fragmentSpread.getName());
            return definition != null && isUnloaded(spi, definition.getSelectionSet());
        }

        private boolean isUnloaded(ImmutableSpi spi, InlineFragment inlineFragment) {
            return isUnloaded(spi, inlineFragment.getSelectionSet());
        }
    }
}
