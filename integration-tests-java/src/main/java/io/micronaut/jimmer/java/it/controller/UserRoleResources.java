package io.micronaut.jimmer.java.it.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.jimmer.java.it.entity.UserRole;
import io.micronaut.jimmer.java.it.entity.dto.UserRoleSpecification;
import io.micronaut.jimmer.java.it.repository.UserRoleRepository;
import io.micronaut.jimmer.java.it.service.IUserRoleService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Controller("/userRoleResources")
public class UserRoleResources {

    private final IUserRoleService iUserRoleService;

    private final UserRoleRepository userRoleRepository;

    public UserRoleResources(
            IUserRoleService iUserRoleService, UserRoleRepository userRoleRepository) {
        this.iUserRoleService = iUserRoleService;
        this.userRoleRepository = userRoleRepository;
    }

    @Get("/userRoleFindById")
    public HttpResponse<UserRole> userRoleFindById(@QueryValue UUID id) {
        return HttpResponse.ok(iUserRoleService.findById(id));
    }

    @Put("/updateUserRoleById")
    @Transactional(rollbackOn = Exception.class)
    public HttpResponse<Void> updateUserRoleById(@QueryValue UUID id) {
        iUserRoleService.updateById(id);
        return HttpResponse.ok();
    }

    @Delete("/delete")
    @Transactional(rollbackOn = Exception.class)
    public HttpResponse<Void> delete(@QueryValue UUID id) {
        iUserRoleService.deleteById(id);
        return HttpResponse.ok();
    }

    @Get("/deleteReverseById")
    public HttpResponse<UserRole> deleteReverseById(@QueryValue UUID id) {
        return HttpResponse.ok(iUserRoleService.deleteReverseById(id));
    }

    @Get("/testUserRoleSpecification")
    public HttpResponse<List<UserRole>> testUserRoleSpecification(
            @RequestBean UserRoleSpecification userRoleSpecification) {
        return HttpResponse.ok(userRoleRepository.find(userRoleSpecification));
    }
}
