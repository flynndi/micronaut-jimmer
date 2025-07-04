package io.micronaut.jimmer.java.it.entity;

import io.micronaut.jimmer.java.it.resolver.BookStoreAvgPriceResolver;
import java.math.BigDecimal;
import java.util.List;
import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

@Entity
public interface BookStore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @Key
    String name();

    @Nullable
    String website();

    @OneToMany(
            mappedBy = "store",
            orderedProps = {@OrderedProp("name"), @OrderedProp(value = "edition", desc = true)})
    List<Book> books();

    @Transient(value = BookStoreAvgPriceResolver.class)
    BigDecimal avgPrice();

    @Transient(ref = "bookStoreNewestBooksResolver")
    List<Book> newestBooks();
}
