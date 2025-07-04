package io.micronaut.jimmer.cfg;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.jimmer.client.CodeBasedExceptionAdvice;
import io.micronaut.jimmer.client.CodeBasedRuntimeExceptionAdvice;
import jakarta.inject.Singleton;

@Factory
@Internal
@Requires(
        property = "micronaut.jimmer.error-translator.disabled",
        value = "false",
        defaultValue = "false")
public class ErrorTranslatorConfig {

    @Singleton
    @Requires(missingBeans = CodeBasedExceptionAdvice.class)
    public CodeBasedExceptionAdvice codeBasedExceptionAdvice(JimmerConfiguration properties) {
        return new CodeBasedExceptionAdvice(properties);
    }

    @Singleton
    @Requires(missingBeans = CodeBasedRuntimeExceptionAdvice.class)
    public CodeBasedRuntimeExceptionAdvice codeBasedRuntimeExceptionAdvice(
            JimmerConfiguration properties) {
        return new CodeBasedRuntimeExceptionAdvice(properties);
    }
}
