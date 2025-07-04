package io.micronaut.jimmer.java.it.repository;

import io.micronaut.jimmer.java.it.config.Constant;
import io.micronaut.jimmer.java.it.entity.UserRole;
import io.micronaut.jimmer.repo.support.AbstractJavaRepository;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.UUID;
import org.babyfish.jimmer.sql.JSqlClient;

@Singleton
public class UserRoleJavaRepository extends AbstractJavaRepository<UserRole, UUID> {
    protected UserRoleJavaRepository(@Named(Constant.DATASOURCE2) JSqlClient sqlClient) {
        super(sqlClient);
    }
}
