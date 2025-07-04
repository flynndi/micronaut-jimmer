package io.micronaut.jimmer.java.it.repository;

import io.micronaut.jimmer.java.it.entity.BookStore;
import io.micronaut.jimmer.repository.JRepository;
import io.micronaut.jimmer.repository.annotation.Repository;

@Repository
public interface BookStoreRepository extends JRepository<BookStore, Long> {}
