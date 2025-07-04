package io.micronaut.jimmer.java.it.parser;

import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.repository.parser.Context;
import io.micronaut.jimmer.repository.parser.Path;
import io.micronaut.jimmer.repository.parser.Source;
import org.babyfish.jimmer.meta.ImmutableType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PathParserTest {

    @Test
    public void testBookName() {
        Path path =
                Path.of(new Context(), false, new Source("Name"), ImmutableType.get(Book.class));
        Assertions.assertEquals("name", path.toString());
    }

    @Test
    public void testTooLongBookName() {
        IllegalArgumentException ex =
                Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                Path.of(
                                        new Context(),
                                        false,
                                        new Source("Name2"),
                                        ImmutableType.get(Book.class)));
        Assertions.assertEquals(
                "Cannot resolve the property name \"[Name2]\" by "
                        + "\"io.micronaut.jimmer.java.it.entity.Book\"",
                ex.getMessage());
    }

    @Test
    public void testTooShortBookName() {
        IllegalArgumentException ex =
                Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                Path.of(
                                        new Context(),
                                        false,
                                        new Source("Nam"),
                                        ImmutableType.get(Book.class)));
        Assertions.assertEquals(
                "Cannot resolve the property name \"[Nam]\" by "
                        + "\"io.micronaut.jimmer.java.it.entity.Book\"",
                ex.getMessage());
    }

    @Test
    public void testPath() {
        Path path =
                Path.of(
                        new Context(),
                        false,
                        new Source("StoreName"),
                        ImmutableType.get(Book.class));
        Assertions.assertEquals("store.name", path.toString());
    }

    @Test
    public void testTooLongPath() {
        IllegalArgumentException ex =
                Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                Path.of(
                                        new Context(),
                                        false,
                                        new Source("StoreName2"),
                                        ImmutableType.get(Book.class)));
        Assertions.assertEquals(
                "Cannot resolve the property name \"Store[Name2]\" by "
                        + "\"io.micronaut.jimmer.java.it.entity.BookStore\"",
                ex.getMessage());
    }

    @Test
    public void testTooShortPath() {
        IllegalArgumentException ex =
                Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                Path.of(
                                        new Context(),
                                        false,
                                        new Source("StoreNam"),
                                        ImmutableType.get(Book.class)));
        Assertions.assertEquals(
                "Cannot resolve the property name \"[StoreNam]\" by "
                        + "\"io.micronaut.jimmer.java.it.entity.Book\"",
                ex.getMessage());
    }
}
