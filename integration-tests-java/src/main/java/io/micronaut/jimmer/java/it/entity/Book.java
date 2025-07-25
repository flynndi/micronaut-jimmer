package io.micronaut.jimmer.java.it.entity;

import java.math.BigDecimal;
import java.util.List;
import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

@Entity
public interface Book extends TenantAware, BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    String name();

    int edition();

    BigDecimal price();

    @IdView
    @Nullable
    Long storeId();

    @Nullable
    @ManyToOne
    BookStore store();

    @ManyToMany(orderedProps = {@OrderedProp("firstName"), @OrderedProp("lastName")})
    @JoinTable(
            name = "BOOK_AUTHOR_MAPPING",
            joinColumnName = "BOOK_ID",
            inverseJoinColumnName = "AUTHOR_ID")
    List<Author> authors();

    @IdView("authors")
    List<Long> authorIds();
}
