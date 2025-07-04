package io.micronaut.jimmer.kotlin.it.controller

import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.jimmer.kotlin.it.entity.dto.TreeNodeDetailView
import io.micronaut.jimmer.kotlin.it.repository.TreeNodeRepository
import org.babyfish.jimmer.client.meta.Api

@Controller("/treeNodeResources")
@Api("TreeNode")
class TreeNodeResources(
    private val treeNodeRepository: TreeNodeRepository,
) {
    @Get("/infiniteRecursion")
    @Api
    fun infiniteRecursion(
        @QueryValue parentId: @Nullable Long?,
    ): HttpResponse<List<TreeNodeDetailView>> = HttpResponse.ok(treeNodeRepository.infiniteRecursion(parentId))

    @Get("/all")
    @Api
    fun all(): HttpResponse<List<TreeNodeDetailView>> = HttpResponse.ok(treeNodeRepository.viewer(TreeNodeDetailView::class).findAll())
}
