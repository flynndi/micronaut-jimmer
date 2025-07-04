package io.micronaut.jimmer.client;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.jimmer.cfg.JimmerConfiguration;
import org.babyfish.jimmer.error.CodeBasedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeBasedRuntimeExceptionAdvice extends CommonExceptionAdvice
        implements ExceptionHandler<CodeBasedRuntimeException, HttpResponse<?>> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CodeBasedRuntimeExceptionAdvice.class);

    JimmerConfiguration jimmerConfiguration;

    public CodeBasedRuntimeExceptionAdvice(JimmerConfiguration properties) {
        super(properties);
    }

    @Override
    public HttpResponse<?> handle(HttpRequest request, CodeBasedRuntimeException exception) {
        LOGGER.error(
                "Auto handled HTTP Error(" + CodeBasedRuntimeException.class.getName() + ")",
                exception);
        return HttpResponse.status(errorTranslator.getHttpStatus(), exception.getMessage())
                .body(resultMap(exception));
    }
}
