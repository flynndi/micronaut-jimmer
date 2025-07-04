package io.micronaut.jimmer.java.it.resolver;

import io.micronaut.context.ApplicationContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.*;
import org.babyfish.jimmer.sql.TransientResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class TransientResolverTestCase {

    @Inject ApplicationContext applicationContext;

    @Test
    void testTransientResolver() {
        Collection<TransientResolver> beansOfType =
                applicationContext.getBeansOfType(TransientResolver.class);
        Assertions.assertNotNull(beansOfType);
        Assertions.assertEquals(2, beansOfType.size());
    }
}
