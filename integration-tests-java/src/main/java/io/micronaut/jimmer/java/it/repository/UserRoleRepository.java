package io.micronaut.jimmer.java.it.repository;

import io.micronaut.jimmer.java.it.config.Constant;
import io.micronaut.jimmer.java.it.entity.Fetchers;
import io.micronaut.jimmer.java.it.entity.Tables;
import io.micronaut.jimmer.java.it.entity.UserRole;
import io.micronaut.jimmer.java.it.entity.dto.UserRoleSpecification;
import io.micronaut.jimmer.repository.JRepository;
import io.micronaut.jimmer.repository.annotation.Repository;
import java.util.List;
import java.util.UUID;

@Repository(dataSourceName = Constant.DATASOURCE2)
public interface UserRoleRepository extends JRepository<UserRole, UUID> {

    default List<UserRole> find(UserRoleSpecification userRoleSpecification) {
        return sql().createQuery(Tables.USER_ROLE_TABLE)
                .where(userRoleSpecification)
                .select(Tables.USER_ROLE_TABLE.fetch(Fetchers.USER_ROLE_FETCHER.allScalarFields()))
                .execute();
    }

    UserRole findByUserId(String userId);

    UserRole findByRoleId(String roleId);

    UserRole findByUserIdAndRoleId(String userId, String roleId);
}
