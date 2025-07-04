package io.micronaut.jimmer.client;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.jimmer.cfg.JimmerConfiguration;
import jakarta.annotation.Nullable;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import org.babyfish.jimmer.client.meta.ApiService;
import org.babyfish.jimmer.client.meta.Schema;
import org.babyfish.jimmer.client.runtime.impl.MetadataBuilder;

@Controller
@Requires(property = "micronaut.jimmer.client.openapi.ui-path")
public class OpenApiUiController {

    private static final String CSS_RESOURCE = "META-INF/jimmer/swagger/swagger-ui.css";

    private static final String JS_RESOURCE = "META-INF/jimmer/swagger/swagger-ui.js";

    private static final String CSS_URL = "/jimmer-client/swagger-ui.css";

    private static final String JS_URL = "/jimmer-client/swagger-ui.js";

    private final JimmerConfiguration properties;

    public OpenApiUiController(JimmerConfiguration properties) {
        this.properties = properties;
    }

    @Get("${micronaut.jimmer.client.openapi.ui-path}")
    public HttpResponse<StreamedFile> download(
            @Nullable @QueryValue(defaultValue = "") String groups) {

        String html = this.html(groups);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        StreamedFile file = new StreamedFile(inputStream, MediaType.of("text/html"));
        return HttpResponse.ok(file);
    }

    private String html(String groups) {
        String refPath = properties.getClient().getOpenapi().getRefPath();
        String resource;
        if (hasMetadata()) {
            resource =
                    refPath != null && !refPath.isEmpty()
                            ? "META-INF/jimmer/openapi/index.html.template"
                            : "META-INF/jimmer/openapi/no-api.html";
        } else {
            resource = "META-INF/jimmer/openapi/no-metadata.html";
        }
        StringBuilder builder = new StringBuilder();
        char[] buf = new char[1024];
        InputStream inputStream =
                OpenApiController.class.getClassLoader().getResourceAsStream(resource);
        assert inputStream != null;
        try (Reader reader = new InputStreamReader(inputStream)) {
            int len;
            if ((len = reader.read(buf)) != -1) {
                builder.append(buf, 0, len);
            }
        } catch (IOException ex) {
            throw new AssertionError("Internal bug: Can read \"" + resource + "\"");
        }
        boolean isTemplate = resource.endsWith(".template");
        if (!isTemplate) {
            return builder.toString();
        }
        if (groups != null && !groups.isEmpty()) {
            try {
                refPath += "?groups=" + URLEncoder.encode(groups, "utf-8");
            } catch (UnsupportedEncodingException ex) {
                throw new AssertionError("Internal bug: utf-8 is not supported");
            }
        }
        return builder.toString()
                .replace(
                        "${openapi.css}",
                        exists(CSS_RESOURCE)
                                ? CSS_URL
                                : "https://unpkg.com/swagger-ui-dist@5.10.5/swagger-ui.css")
                .replace(
                        "${openapi.js}",
                        exists(JS_RESOURCE)
                                ? JS_URL
                                : "https://unpkg.com/swagger-ui-dist@5.10.5/swagger-ui-bundle.js")
                .replace("${openapi.refPath}", refPath);
    }

    private boolean hasMetadata() {
        Schema schema = MetadataBuilder.loadSchema(Collections.emptySet());
        for (ApiService service : schema.getApiServiceMap().values()) {
            if (!service.getOperations().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Get(CSS_URL)
    public HttpResponse<StreamedFile> css() throws IOException {
        return downloadResource(CSS_RESOURCE, MediaType.of("text/css"));
    }

    @Get(JS_URL)
    public HttpResponse<StreamedFile> js() throws IOException {

        return downloadResource(JS_RESOURCE, MediaType.of("text/javascript"));
    }

    private HttpResponse<StreamedFile> downloadResource(String resource, MediaType mediaType)
            throws IOException {
        byte[] buf = new byte[4 * 1024];
        InputStream in = OpenApiController.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            throw new IllegalStateException("The resource \"" + resource + "\" does not exist");
        }
        StreamedFile file = new StreamedFile(in, mediaType);
        return HttpResponse.ok(file);
    }

    private static boolean exists(String resource) {
        Enumeration<URL> enumeration;
        try {
            enumeration = OpenApiController.class.getClassLoader().getResources(resource);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to check the existence of resource \"" + resource + "\"");
        }
        return enumeration.hasMoreElements();
    }
}
