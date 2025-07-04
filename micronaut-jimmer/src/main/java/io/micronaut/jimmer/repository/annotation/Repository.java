package io.micronaut.jimmer.repository.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Repository {

    String dataSourceName() default "default";
}
