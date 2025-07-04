package io.micronaut.jimmer.java.it.repository;

import io.micronaut.jimmer.java.it.entity.Book;
import io.micronaut.jimmer.repo.support.AbstractJavaRepository;
import jakarta.inject.Singleton;
import org.babyfish.jimmer.sql.JSqlClient;

@Singleton
public class BookJavaRepository extends AbstractJavaRepository<Book, Long> {
    protected BookJavaRepository(JSqlClient sqlClient) {
        super(sqlClient);
    }

    Book methodInBookJavaRepositoryFindById(Long id) {
        return sql.findById(Book.class, id);
    }
}
