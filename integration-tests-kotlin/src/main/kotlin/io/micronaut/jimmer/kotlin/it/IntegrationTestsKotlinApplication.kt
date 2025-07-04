package io.micronaut.jimmer.kotlin.it

import io.micronaut.runtime.Micronaut

class IntegrationTestsKotlinApplication {
    companion object {
        @JvmStatic
        fun main(array: Array<String>) {
            Micronaut.run(IntegrationTestsKotlinApplication::class.java)
        }
    }
}
