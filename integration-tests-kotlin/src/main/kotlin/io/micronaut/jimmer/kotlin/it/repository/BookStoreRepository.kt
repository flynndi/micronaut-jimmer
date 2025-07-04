package io.micronaut.jimmer.kotlin.it.repository

import io.micronaut.jimmer.kotlin.it.entity.BookStore
import io.micronaut.jimmer.repository.KRepository
import io.micronaut.jimmer.repository.annotation.Repository

@Repository
interface BookStoreRepository : KRepository<BookStore, Long>
