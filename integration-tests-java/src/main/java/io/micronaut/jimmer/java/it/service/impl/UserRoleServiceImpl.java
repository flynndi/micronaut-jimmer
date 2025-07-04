package io.micronaut.jimmer.java.it.service.impl;

import io.micronaut.jimmer.java.it.config.Constant;
import io.micronaut.jimmer.java.it.entity.Tables;
import io.micronaut.jimmer.java.it.entity.UserRole;
import io.micronaut.jimmer.java.it.repository.UserRoleRepository;
import io.micronaut.jimmer.java.it.service.IUserRoleService;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.UUID;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.runtime.LogicalDeletedBehavior;

@Singleton
public class UserRoleServiceImpl implements IUserRoleService {

    private final UserRoleRepository userRoleRepository;

    private final JSqlClient jSqlClientDB2;

    public UserRoleServiceImpl(
            UserRoleRepository userRoleRepository,
            @Named(Constant.DATASOURCE2) JSqlClient jSqlClientDB2) {
        this.userRoleRepository = userRoleRepository;
        this.jSqlClientDB2 = jSqlClientDB2;
    }

    @Override
    public UserRole findById(UUID id) {
        return jSqlClientDB2.findById(UserRole.class, id);
    }

    @Override
    public void updateById(UUID id) {
        jSqlClientDB2
                .createUpdate(Tables.USER_ROLE_TABLE)
                .set(Tables.USER_ROLE_TABLE.roleId(), "123")
                .where(Tables.USER_ROLE_TABLE.id().eq(id))
                .execute();
    }

    @Override
    public void deleteById(UUID id) {
        userRoleRepository.deleteById(id);
    }

    @Override
    public UserRole deleteReverseById(UUID id) {
        return jSqlClientDB2
                .filters(cfg -> cfg.setBehavior(LogicalDeletedBehavior.REVERSED))
                .findById(UserRole.class, id);
    }
}
