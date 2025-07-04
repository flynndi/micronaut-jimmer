package io.micronaut.jimmer.kotlin.it.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.GenerationType
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.JoinTable
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToMany
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.OrderedProp
import java.math.BigDecimal

@Entity
interface Book :
    BaseEntity,
    TenantAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    @Key
    val name: String

    @Key
    val edition: Int

    val price: BigDecimal

    @IdView
    val storeId: Long?

    @ManyToOne
    val store: BookStore?

    @ManyToMany(orderedProps = [OrderedProp("firstName"), OrderedProp("lastName")])
    @JoinTable(name = "BOOK_AUTHOR_MAPPING", joinColumnName = "BOOK_ID", inverseJoinColumnName = "AUTHOR_ID")
    val authors: List<Author>

    @IdView("authors")
    val authorsIds: List<Long>
}
