package io.micronaut.jimmer.java.it.parser;

import io.micronaut.data.model.Pageable;
import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.repository.JRepository;
import io.micronaut.jimmer.repository.parser.Context;
import io.micronaut.jimmer.repository.parser.QueryMethod;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueryMethodParserTest {

    @Test
    public void testEntityMethod() throws NoSuchMethodException {

        Method method =
                Dao.class.getMethod(
                        "findByNameOrderByName", String.class, Pageable.class, Fetcher.class);
        QueryMethod queryMethod1 =
                QueryMethod.of(new Context(), ImmutableType.get(Book.class), method);
        Assertions.assertNotNull(queryMethod1);
        method =
                Dao.class.getMethod(
                        "findByNameAndEditionInOrderByNameAscEditionDesc",
                        String.class,
                        Collection.class);
        QueryMethod queryMethod =
                QueryMethod.of(new Context(), ImmutableType.get(Book.class), method);
        Assertions.assertNotNull(queryMethod);
    }

    interface Dao extends JRepository<Book, Long> {

        // Dynamic entity
        Page<Book> findByNameOrderByName(String name, Pageable pagination, Fetcher<Book> fetcher);

        List<Book> findByNameAndEditionInOrderByNameAscEditionDesc(
                String name, Collection<Integer> editions // Test boxing for element type
                );
    }

    private static void assertQueryMethod(QueryMethod queryMethod, String expectedText) {
        Assertions.assertEquals(
                expectedText.replace("\r", "").replace("\n", "").replace("--->", ""),
                queryMethod.toString());
    }
}
