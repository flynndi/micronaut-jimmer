package io.micronaut.jimmer.java.it.config;

import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.*;
import graphql.schema.idl.*;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.jimmer.graphql.DataFetchingEnvironments;
import io.micronaut.jimmer.java.it.entity.*;
import jakarta.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.babyfish.jimmer.sql.JSqlClient;

@Factory
public class GraphQLFactory {

    @Bean
    @Singleton
    public GraphQL graphQL(
            ResourceResolver resourceResolver, BookStoreDataFetcher bookStoreDataFetcher) {
        TypeResolver commonEntityTypeResolver =
                env -> {
                    Object javaObject = env.getObject();
                    if (javaObject instanceof Book) {
                        return env.getSchema().getObjectType("Book");
                    } else if (javaObject instanceof BookStore) {
                        return env.getSchema().getObjectType("BookStore");
                    } else if (javaObject instanceof Author) {
                        return env.getSchema().getObjectType("Author");
                    }
                    return null;
                };
        SchemaParser schemaParser = new SchemaParser();
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
        typeRegistry.merge(
                schemaParser.parse(
                        new BufferedReader(
                                new InputStreamReader(
                                        resourceResolver
                                                .getResourceAsStream("classpath:schema.graphqls")
                                                .get()))));

        RuntimeWiring.Builder wiringBuilder =
                RuntimeWiring.newRuntimeWiring()
                        .type(
                                "Query",
                                typeWiring ->
                                        typeWiring.dataFetcher("bookStores", bookStoreDataFetcher))
                        .scalar(ExtendedScalars.GraphQLLong)
                        .scalar(ExtendedScalars.GraphQLBigDecimal)
                        .scalar(ExtendedScalars.DateTime)
                        .scalar(LocalDateTimeScalar.INSTANCE)
                        .type(
                                "CommonEntity",
                                builder -> builder.typeResolver(commonEntityTypeResolver));

        RuntimeWiring runtimeWiring = wiringBuilder.build();

        GraphQLSchema graphQLSchema =
                schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);

        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    public static class LocalDateTimeScalar {

        public static final GraphQLScalarType INSTANCE =
                GraphQLScalarType.newScalar()
                        .name("LocalDateTime")
                        .description("A custom scalar that handles Java 8 LocalDateTime")
                        .coercing(
                                new Coercing<LocalDateTime, String>() {
                                    private final DateTimeFormatter formatter =
                                            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

                                    @Override
                                    public String serialize(Object dataFetcherResult) {
                                        return ((LocalDateTime) dataFetcherResult)
                                                .format(formatter);
                                    }

                                    @Override
                                    public LocalDateTime parseValue(Object input) {
                                        return LocalDateTime.parse(input.toString(), formatter);
                                    }

                                    @Override
                                    public LocalDateTime parseLiteral(Object input) {
                                        return LocalDateTime.parse(input.toString(), formatter);
                                    }
                                })
                        .build();
    }

    @Singleton
    public static class BookStoreDataFetcher implements DataFetcher<Object> {

        private final JSqlClient sqlClient;

        public BookStoreDataFetcher(JSqlClient sqlClient) {
            this.sqlClient = sqlClient;
        }

        @Override
        public Object get(DataFetchingEnvironment environment) throws Exception {
            String name = environment.getArgument("name");
            BookStoreTable table = Tables.BOOK_STORE_TABLE;
            return sqlClient
                    .createQuery(table)
                    .where(table.name().ilikeIf(name))
                    .select(
                            table.fetch(
                                    DataFetchingEnvironments.createFetcher(
                                            BookStore.class, environment)))
                    .execute();
        }
    }
}
