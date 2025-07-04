package io.micronaut.jimmer.java.it.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import org.babyfish.jimmer.sql.MappedSuperclass;

@MappedSuperclass
public interface BaseEntity {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdTime();

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime modifiedTime();
}
