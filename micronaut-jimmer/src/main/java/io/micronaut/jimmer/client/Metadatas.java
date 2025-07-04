package io.micronaut.jimmer.client;

import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.FileUpload;
import io.micronaut.http.multipart.PartData;
import io.micronaut.http.netty.stream.StreamedHttpResponse;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;
import org.babyfish.jimmer.client.meta.TypeName;
import org.babyfish.jimmer.client.runtime.Metadata;
import org.babyfish.jimmer.client.runtime.Operation;
import org.babyfish.jimmer.client.runtime.VirtualType;
import org.jetbrains.annotations.Nullable;

public class Metadatas {

    private static final Pattern COMMA_PATTERN = Pattern.compile("\\s*,\\s*");

    private Metadatas() {}

    public static Metadata create(
            boolean isGenericSupported, @Nullable String groups, @Nullable String uriPrefix) {
        return Metadata.newBuilder()
                .setOperationParser(new OperationParserImpl())
                .setParameterParser(new ParameterParserImpl())
                .setVirtualTypeMap(
                        Collections.singletonMap(TypeName.of(FileUpload.class), VirtualType.FILE))
                .setGenericSupported(isGenericSupported)
                .setGroups(
                        groups != null && !groups.isEmpty()
                                ? Arrays.asList(COMMA_PATTERN.split(groups))
                                : null)
                .setUriPrefix(uriPrefix)
                .build();
    }

    private static class OperationParserImpl implements Metadata.OperationParser {

        @Override
        public String uri(AnnotatedElement element) {
            Controller uriMapping = element.getAnnotation(Controller.class);
            if (uriMapping != null) {
                String uri = uriMapping.value();
                if (uri != null) {
                    return uri;
                }
            }
            Get getMapping = element.getAnnotation(Get.class);
            if (getMapping != null) {
                String uri = getMapping.value();
                if (uri != null) {
                    return uri;
                }
            }
            Post postMapping = element.getAnnotation(Post.class);
            if (postMapping != null) {
                String uri = postMapping.value();
                if (uri != null) {
                    return uri;
                }
            }
            Put putMapping = element.getAnnotation(Put.class);
            if (putMapping != null) {
                String uri = putMapping.value();
                if (uri != null) {
                    return uri;
                }
            }
            Delete deleteMapping = element.getAnnotation(Delete.class);
            if (deleteMapping != null) {
                String uri = deleteMapping.value();
                if (uri != null) {
                    return uri;
                }
            }
            Patch patchMapping = element.getAnnotation(Patch.class);
            if (patchMapping != null) {
                String uri = patchMapping.value();
                if (uri != null) {
                    return uri;
                }
            }
            return null;
        }

        @Override
        public Operation.HttpMethod[] http(Method method) {
            if (method.getAnnotation(Post.class) != null) {
                return new Operation.HttpMethod[] {Operation.HttpMethod.POST};
            }
            if (method.getAnnotation(Put.class) != null) {
                return new Operation.HttpMethod[] {Operation.HttpMethod.PUT};
            }
            if (method.getAnnotation(Delete.class) != null) {
                return new Operation.HttpMethod[] {Operation.HttpMethod.DELETE};
            }
            if (method.getAnnotation(Patch.class) != null) {
                return new Operation.HttpMethod[] {Operation.HttpMethod.PATCH};
            }
            return new Operation.HttpMethod[] {Operation.HttpMethod.GET};
        }

        @Override
        public boolean isStream(Method method) {
            return StreamedHttpResponse.class == method.getReturnType();
        }
    }

    private static class ParameterParserImpl implements Metadata.ParameterParser {

        @Nullable
        @Override
        public String requestHeader(Parameter javaParameter) {
            Header requestHeader = javaParameter.getAnnotation(Header.class);
            if (requestHeader == null) {
                return null;
            }
            String name = requestHeader.value();
            if (name.isEmpty()) {
                name = requestHeader.name();
            }
            return name;
        }

        @Nullable
        @Override
        public String requestParam(Parameter javaParameter) {
            QueryValue requestParam = javaParameter.getAnnotation(QueryValue.class);
            if (requestParam == null) {
                return null;
            }
            return requestParam.value();
        }

        @Nullable
        @Override
        public String pathVariable(Parameter javaParameter) {
            PathVariable pathVariable = javaParameter.getAnnotation(PathVariable.class);
            if (pathVariable == null) {
                return null;
            }
            String name = pathVariable.value();
            if (name.isEmpty()) {
                name = pathVariable.name();
            }
            return name;
        }

        @Nullable
        @Override
        public String requestPart(Parameter javaParameter) {
            Part requestPart = javaParameter.getAnnotation(Part.class);
            if (requestPart == null) {
                return null;
            }
            return requestPart.value();
        }

        @Override
        public String defaultValue(Parameter javaParameter) {
            Header requestHeader = javaParameter.getAnnotation(Header.class);
            if (requestHeader != null && !requestHeader.defaultValue().isEmpty()) {
                return requestHeader.defaultValue();
            }
            QueryValue requestParam = javaParameter.getAnnotation(QueryValue.class);
            if (requestParam != null && !requestParam.defaultValue().isEmpty()) {
                return requestParam.defaultValue();
            }
            return null;
        }

        @Override
        public Boolean isOptional(Parameter javaParameter) {
            return true;
        }

        @Override
        public boolean isRequestBody(Parameter javaParameter) {
            return javaParameter.isAnnotationPresent(Body.class);
        }

        @Override
        public boolean isRequestPartRequired(Parameter javaParameter) {
            Class<?> type = javaParameter.getType();
            return type == PartData.class || type == PartData[].class;
        }
    }
}
