package io.micronaut.jimmer.kotlin.it.service

import io.micronaut.jimmer.kotlin.it.entity.BookStore

interface IBookStore {
    fun oneToMany(): List<BookStore>
}
