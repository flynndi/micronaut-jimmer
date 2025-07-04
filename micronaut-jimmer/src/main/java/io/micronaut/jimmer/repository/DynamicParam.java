package io.micronaut.jimmer.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This method should only be used to decorate the parameters of abstract methods in derived
 * interfaces of {@link JRepository} or {@link KRepository}.
 *
 * <p>When a parameter is null
 *
 * <ul>
 *   <li>If this annotation is present, the SQL condition is ignored, meaning dynamic query is
 *       performed.
 *   <li>If this annotation is not present, {@link NullPointerException} will be raised
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DynamicParam {}
