package io.micronaut.jimmer.kotlin.it.entity

import io.micronaut.jimmer.kotlin.it.resolver.BookStoreNewestBooksResolver
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.GenerationType
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.OneToMany
import org.babyfish.jimmer.sql.OrderedProp
import java.math.BigDecimal

@Entity
interface BookStore : BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    val name: String

    val website: String?

    @OneToMany(mappedBy = "store", orderedProps = [OrderedProp("name"), OrderedProp(value = "edition", desc = true)])
    val books: List<Book>

    @org.babyfish.jimmer.sql.Transient(ref = "bookStoreAvgPriceResolver")
    val avgPrice: BigDecimal

    @org.babyfish.jimmer.sql.Transient(BookStoreNewestBooksResolver::class)
    val newestBooks: List<Book>
}
