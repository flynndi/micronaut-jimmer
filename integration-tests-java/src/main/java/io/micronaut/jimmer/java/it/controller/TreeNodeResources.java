package io.micronaut.jimmer.java.it.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.jimmer.java.it.entity.dto.TreeNodeDetailView;
import io.micronaut.jimmer.java.it.repository.TreeNodeRepository;
import java.util.List;
import org.babyfish.jimmer.client.meta.Api;

@Controller("/treeNodeResources")
@Api("TreeNode")
public class TreeNodeResources {

    private final TreeNodeRepository treeNodeRepository;

    public TreeNodeResources(TreeNodeRepository treeNodeRepository) {
        this.treeNodeRepository = treeNodeRepository;
    }

    @Get("/infiniteRecursion")
    @Api
    public HttpResponse<List<TreeNodeDetailView>> infiniteRecursion(
            @QueryValue @Nullable Long parentId) {
        return HttpResponse.ok(treeNodeRepository.infiniteRecursion(parentId));
    }

    @Get("/all")
    @Api
    public HttpResponse<List<TreeNodeDetailView>> all() {
        return HttpResponse.ok(treeNodeRepository.viewer(TreeNodeDetailView.class).findAll());
    }
}
