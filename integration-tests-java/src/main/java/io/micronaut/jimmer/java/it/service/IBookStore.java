package io.micronaut.jimmer.java.it.service;

import io.micronaut.jimmer.java.it.entity.BookStore;
import java.util.List;

public interface IBookStore {

    List<BookStore> oneToMany();
}
