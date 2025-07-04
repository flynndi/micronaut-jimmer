package io.micronaut.jimmer.kotlin.it.repository

import io.micronaut.jimmer.kotlin.it.entity.TreeNode
import io.micronaut.jimmer.kotlin.it.entity.dto.TreeNodeDetailView
import io.micronaut.jimmer.kotlin.it.entity.parentId
import io.micronaut.jimmer.repository.KRepository
import io.micronaut.jimmer.repository.annotation.Repository
import org.babyfish.jimmer.sql.kt.ast.expression.`eq?`

@Repository
interface TreeNodeRepository : KRepository<TreeNode, Long> {
    fun infiniteRecursion(parentId: Long?): List<TreeNodeDetailView> =
        sql
            .createQuery(TreeNode::class) {
                where(table.parentId `eq?` parentId)
                select(table.fetch(TreeNodeDetailView::class))
            }.execute()
}
