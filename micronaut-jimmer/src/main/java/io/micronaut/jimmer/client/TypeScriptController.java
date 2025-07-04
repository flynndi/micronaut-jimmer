package io.micronaut.jimmer.client;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.jimmer.cfg.JimmerConfiguration;
import jakarta.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import org.babyfish.jimmer.client.generator.ts.TypeScriptContext;
import org.babyfish.jimmer.client.runtime.Metadata;

@Controller
@Requires(property = "micronaut.jimmer.client.ts.path")
public class TypeScriptController {

    private final JimmerConfiguration properties;

    public TypeScriptController(JimmerConfiguration properties) {
        this.properties = properties;
    }

    @Get("${micronaut.jimmer.client.ts.path}")
    public HttpResponse<byte[]> download(@Nullable @QueryValue(defaultValue = "") String groups) {
        JimmerConfiguration.Client.TypeScript ts = properties.getClient().getTs();
        Metadata metadata = Metadatas.create(true, groups, properties.getClient().getUriPrefix());
        TypeScriptContext ctx =
                new TypeScriptContext(
                        metadata,
                        ts.getIndent(),
                        ts.isMutable(),
                        ts.getApiName(),
                        ts.getNullRenderMode(),
                        ts.isEnumTsStyle());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ctx.renderAll(baos);
        byte[] zipBytes = baos.toByteArray();
        return HttpResponse.ok().header("Content-Type", "application/zip").body(zipBytes);
    }
}
