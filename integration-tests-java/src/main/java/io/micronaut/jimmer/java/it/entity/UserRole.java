package io.micronaut.jimmer.java.it.entity;

import io.micronaut.jimmer.java.it.config.UUIDGenerator;
import io.micronaut.jimmer.java.it.config.jsonmapping.AuthUser;
import jakarta.annotation.Nullable;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.LogicalDeleted;

@Entity
public interface UserRole {

    @Id
    @GeneratedValue(generatorType = UUIDGenerator.class)
    UUID id();

    String userId();

    String roleId();

    @LogicalDeleted("true")
    boolean deleteFlag();

    @Nullable
    AuthUser authUser();
}
