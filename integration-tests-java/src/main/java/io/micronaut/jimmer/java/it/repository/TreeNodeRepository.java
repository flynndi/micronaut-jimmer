package io.micronaut.jimmer.java.it.repository;

import io.micronaut.jimmer.java.it.entity.Tables;
import io.micronaut.jimmer.java.it.entity.TreeNode;
import io.micronaut.jimmer.java.it.entity.dto.TreeNodeDetailView;
import io.micronaut.jimmer.repository.JRepository;
import io.micronaut.jimmer.repository.annotation.Repository;
import java.util.List;

@Repository
public interface TreeNodeRepository extends JRepository<TreeNode, Long> {

    default List<TreeNodeDetailView> infiniteRecursion(Long parentId) {
        return sql().createQuery(Tables.TREE_NODE_TABLE)
                .where(Tables.TREE_NODE_TABLE.parentId().eq(parentId))
                .select(Tables.TREE_NODE_TABLE.fetch(TreeNodeDetailView.class))
                .execute();
    }
}
