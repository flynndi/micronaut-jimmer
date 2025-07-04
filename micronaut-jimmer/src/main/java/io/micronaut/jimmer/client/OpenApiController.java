package io.micronaut.jimmer.client;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.jimmer.cfg.JimmerConfiguration;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.babyfish.jimmer.client.generator.openapi.OpenApiGenerator;
import org.babyfish.jimmer.client.generator.openapi.OpenApiProperties;
import org.babyfish.jimmer.client.runtime.Metadata;

@Controller
@Requires(property = "micronaut.jimmer.client.openapi.path")
public class OpenApiController {

    private final JimmerConfiguration properties;

    public OpenApiController(JimmerConfiguration properties) {
        this.properties = properties;
    }

    @Get("${micronaut.jimmer.client.openapi.path}")
    public HttpResponse<Writable> download(@Nullable @QueryValue(defaultValue = "") String groups) {
        Metadata metadata = Metadatas.create(false, groups, properties.getClient().getUriPrefix());
        OpenApiProperties openApiProperties = getOpenApiProperties();

        OpenApiGenerator generator =
                new OpenApiGenerator(metadata, openApiProperties) {
                    @Override
                    protected int errorHttpStatus() {
                        return properties.getErrorTranslator().getHttpStatus();
                    }
                };
        Writable body = generator::generate;
        return HttpResponse.ok(body).header(HttpHeaders.CONTENT_TYPE, "application/yml");
    }

    private OpenApiProperties getOpenApiProperties() {
        var clientProps = properties.getClient().getOpenapi().getProperties();

        List<OpenApiProperties.Server> servers = null;
        var propServers = clientProps.getServers();
        if (propServers != null && !propServers.isEmpty()) {
            servers = new ArrayList<>(propServers.size());
            for (var server : propServers) {
                Map<String, OpenApiProperties.Variable> vars = new HashMap<>();
                if (server.getVariables() != null) {
                    server.getVariables()
                            .forEach(
                                    (k, v) ->
                                            vars.put(
                                                    k,
                                                    new OpenApiProperties.Variable(
                                                            v.getEnums(),
                                                            v.getDefaultValue(),
                                                            v.getDescription())));
                }
                servers.add(
                        new OpenApiProperties.Server(
                                server.getUrl(), server.getDescription(), vars));
            }
        }

        Map<String, OpenApiProperties.SecurityScheme> securitySchemes = null;
        var propSchemes = clientProps.getComponents().getSecuritySchemes();
        if (propSchemes != null && !propSchemes.isEmpty()) {
            securitySchemes = new HashMap<>();
            for (var entry : propSchemes.entrySet()) {
                var v = entry.getValue();
                var flows = v.getFlows();
                OpenApiProperties.Flows openApiFlows =
                        new OpenApiProperties.Flows(
                                toFlow(flows.getImplicit()),
                                toFlow(flows.getPassword()),
                                toFlow(flows.getClientCredentials()),
                                toFlow(flows.getAuthorizationCode()));
                securitySchemes.put(
                        entry.getKey(),
                        new OpenApiProperties.SecurityScheme(
                                v.getType(),
                                v.getDescription(),
                                v.getName(),
                                v.getIn(),
                                v.getScheme(),
                                v.getBearerFormat(),
                                openApiFlows,
                                v.getOpenIdConnectUrl()));
            }
        }

        var info = clientProps.getInfo();
        var contact = info.getContact();
        var license = info.getLicense();

        return OpenApiProperties.newBuilder()
                .setInfo(
                        new OpenApiProperties.Info(
                                info.getTitle(),
                                info.getDescription(),
                                info.getTermsOfService(),
                                new OpenApiProperties.Contact(
                                        contact.getName(), contact.getUrl(), contact.getEmail()),
                                new OpenApiProperties.License(
                                        license.getName(), license.getIdentifier()),
                                info.getVersion()))
                .setServers(servers)
                .setComponents(new OpenApiProperties.Components(securitySchemes))
                .setSecurities(clientProps.getSecurities())
                .build();
    }

    private OpenApiProperties.Flow toFlow(
            JimmerConfiguration.Client.Openapi.OpenApiProperties.Components.SecurityScheme.Flows
                            .Flow
                    src) {
        if (src == null) return null;
        return new OpenApiProperties.Flow(
                src.getAuthorizationUrl(), src.getTokenUrl(), src.getRefreshUrl(), src.getScopes());
    }
}
