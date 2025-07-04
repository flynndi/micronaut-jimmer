package io.micronaut.jimmer.client;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.jimmer.cfg.JimmerConfiguration;
import org.babyfish.jimmer.error.CodeBasedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeBasedExceptionAdvice extends CommonExceptionAdvice
        implements ExceptionHandler<CodeBasedException, HttpResponse<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeBasedExceptionAdvice.class);

    JimmerConfiguration.ErrorTranslator errorTranslator;

    public CodeBasedExceptionAdvice(JimmerConfiguration properties) {
        super(properties);
    }

    @Override
    public HttpResponse<?> handle(HttpRequest request, CodeBasedException exception) {
        LOGGER.error(
                "Auto handled HTTP Error(" + CodeBasedException.class.getName() + ")", exception);
        return HttpResponse.status(errorTranslator.getHttpStatus(), exception.getMessage())
                .body(resultMap(exception));
    }
}
