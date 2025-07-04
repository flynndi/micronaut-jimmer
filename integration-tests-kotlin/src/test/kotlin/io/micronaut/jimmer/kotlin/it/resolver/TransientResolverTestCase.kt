package io.micronaut.jimmer.kotlin.it.resolver

import io.micronaut.context.ApplicationContext
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.babyfish.jimmer.sql.TransientResolver
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
class TransientResolverTestCase {
    @Inject
    lateinit var applicationContext: ApplicationContext

    @Test
    fun testTransientResolver() {
        val beansOfType = applicationContext.getBeansOfType(TransientResolver::class.java)
        Assertions.assertNotNull(beansOfType)
        Assertions.assertEquals(2, beansOfType.size)
    }
}
