package io.micronaut.jimmer.java.it.service;

import io.micronaut.jimmer.java.it.entity.UserRole;
import java.util.UUID;

public interface IUserRoleService {

    UserRole findById(UUID id);

    void updateById(UUID id);

    void deleteById(UUID id);

    UserRole deleteReverseById(UUID id);
}
