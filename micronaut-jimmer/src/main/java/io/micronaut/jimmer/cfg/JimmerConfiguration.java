package io.micronaut.jimmer.cfg;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.core.convert.ConversionContext;
import io.micronaut.core.convert.TypeConverter;
import jakarta.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.babyfish.jimmer.client.generator.ts.NullRenderMode;
import org.babyfish.jimmer.sql.dialect.Dialect;

@ConfigurationProperties("micronaut.jimmer")
public class JimmerConfiguration {

    private String language;

    private String transactionCacheOperatorFixedDelay = "5s";

    private ErrorTranslator errorTranslator = new ErrorTranslator();

    private String microServiceName = "";

    private Client client;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTransactionCacheOperatorFixedDelay() {
        return transactionCacheOperatorFixedDelay;
    }

    public void setTransactionCacheOperatorFixedDelay(String transactionCacheOperatorFixedDelay) {
        this.transactionCacheOperatorFixedDelay = transactionCacheOperatorFixedDelay;
    }

    public ErrorTranslator getErrorTranslator() {
        return errorTranslator;
    }

    public void setErrorTranslator(ErrorTranslator errorTranslator) {
        this.errorTranslator = errorTranslator;
    }

    public String getMicroServiceName() {
        return microServiceName;
    }

    public void setMicroServiceName(String microServiceName) {
        this.microServiceName = microServiceName;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @ConfigurationProperties("errorTranslator")
    public static class ErrorTranslator {

        private boolean disabled;

        private int httpStatus = 500;

        private boolean debugInfoSupported;

        private int debugInfoMaxStackTraceCount = Integer.MAX_VALUE;

        public boolean isDisabled() {
            return disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public int getHttpStatus() {
            return httpStatus;
        }

        public void setHttpStatus(int httpStatus) {
            this.httpStatus = httpStatus;
        }

        public boolean isDebugInfoSupported() {
            return debugInfoSupported;
        }

        public void setDebugInfoSupported(boolean debugInfoSupported) {
            this.debugInfoSupported = debugInfoSupported;
        }

        public int getDebugInfoMaxStackTraceCount() {
            return debugInfoMaxStackTraceCount;
        }

        public void setDebugInfoMaxStackTraceCount(int debugInfoMaxStackTraceCount) {
            this.debugInfoMaxStackTraceCount = debugInfoMaxStackTraceCount;
        }
    }

    @ConfigurationProperties("client")
    public static class Client {

        private String uriPrefix;

        private TypeScript ts = new TypeScript();

        private Openapi openapi = new Openapi();

        public String getUriPrefix() {
            return uriPrefix;
        }

        public void setUriPrefix(String uriPrefix) {
            this.uriPrefix = uriPrefix;
        }

        public TypeScript getTs() {
            return ts;
        }

        public void setTs(TypeScript ts) {
            this.ts = ts;
        }

        public Openapi getOpenapi() {
            return openapi;
        }

        public void setOpenapi(Openapi openapi) {
            this.openapi = openapi;
        }

        @ConfigurationProperties("ts")
        public static class TypeScript {

            private String path;

            private String apiName = "Api";

            private int indent = 4;

            private boolean mutable;

            private NullRenderMode nullRenderMode = NullRenderMode.UNDEFINED;

            private boolean isEnumTsStyle;

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getApiName() {
                return apiName;
            }

            public void setApiName(String apiName) {
                this.apiName = apiName;
            }

            public int getIndent() {
                return indent;
            }

            public void setIndent(int indent) {
                this.indent = indent;
            }

            public boolean isMutable() {
                return mutable;
            }

            public void setMutable(boolean mutable) {
                this.mutable = mutable;
            }

            public NullRenderMode getNullRenderMode() {
                return nullRenderMode;
            }

            public void setNullRenderMode(NullRenderMode nullRenderMode) {
                this.nullRenderMode = nullRenderMode;
            }

            public boolean isEnumTsStyle() {
                return isEnumTsStyle;
            }

            public void setEnumTsStyle(boolean enumTsStyle) {
                isEnumTsStyle = enumTsStyle;
            }
        }

        @ConfigurationProperties("openapi")
        public static class Openapi {

            private String path = "/openapi.yml";

            private String uiPath = "/openapi.html";

            private String refPath;

            private OpenApiProperties properties = new OpenApiProperties();

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getUiPath() {
                return uiPath;
            }

            public void setUiPath(String uiPath) {
                this.uiPath = uiPath;
            }

            public String getRefPath() {
                if (refPath == null || refPath.isEmpty()) {
                    if (path == null || path.isEmpty()) {
                        this.refPath = null;
                    } else {
                        this.refPath = path;
                    }
                } else {
                    this.refPath = refPath;
                }
                return refPath;
            }

            public void setRefPath(String refPath) {
                this.refPath = refPath;
            }

            public OpenApiProperties getProperties() {
                return properties;
            }

            public void setProperties(OpenApiProperties properties) {
                this.properties = properties;
            }

            @ConfigurationProperties("properties")
            public static class OpenApiProperties {

                private Info info = new Info();

                private List<Server> servers;

                private List<Map<String, List<String>>> securities;

                private Components components = new Components();

                public Info getInfo() {
                    return info;
                }

                public void setInfo(Info info) {
                    this.info = info;
                }

                public List<Server> getServers() {
                    return servers;
                }

                public void setServers(List<Server> servers) {
                    this.servers = servers;
                }

                public List<Map<String, List<String>>> getSecurities() {
                    return securities;
                }

                public void setSecurities(List<Map<String, List<String>>> securities) {
                    this.securities = securities;
                }

                public Components getComponents() {
                    return components;
                }

                public void setComponents(Components components) {
                    this.components = components;
                }

                @ConfigurationProperties("info")
                public static class Info {

                    private String title;

                    private String description;

                    private String termsOfService;

                    private Contact contact = new Contact();

                    private License license = new License();

                    private String version;

                    public String getTitle() {
                        return title;
                    }

                    public void setTitle(String title) {
                        this.title = title;
                    }

                    public String getDescription() {
                        return description;
                    }

                    public void setDescription(String description) {
                        this.description = description;
                    }

                    public String getTermsOfService() {
                        return termsOfService;
                    }

                    public void setTermsOfService(String termsOfService) {
                        this.termsOfService = termsOfService;
                    }

                    public Contact getContact() {
                        return contact;
                    }

                    public void setContact(Contact contact) {
                        this.contact = contact;
                    }

                    public License getLicense() {
                        return license;
                    }

                    public void setLicense(License license) {
                        this.license = license;
                    }

                    public String getVersion() {
                        return version;
                    }

                    public void setVersion(String version) {
                        this.version = version;
                    }

                    @ConfigurationProperties("contact")
                    public static class Contact {

                        private String name;

                        private String url;

                        private String email;

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }

                        public String getUrl() {
                            return url;
                        }

                        public void setUrl(String url) {
                            this.url = url;
                        }

                        public String getEmail() {
                            return email;
                        }

                        public void setEmail(String email) {
                            this.email = email;
                        }
                    }

                    @ConfigurationProperties("license")
                    public static class License {

                        private String name;

                        private String identifier;

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }

                        public String getIdentifier() {
                            return identifier;
                        }

                        public void setIdentifier(String identifier) {
                            this.identifier = identifier;
                        }
                    }
                }

                @EachProperty(value = "servers", list = true)
                public static class Server {

                    private String url;

                    private String description;

                    private Map<String, Variable> variables;

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public String getDescription() {
                        return description;
                    }

                    public void setDescription(String description) {
                        this.description = description;
                    }

                    public Map<String, Variable> getVariables() {
                        return variables;
                    }

                    public void setVariables(Map<String, Variable> variables) {
                        this.variables = variables;
                    }

                    @ConfigurationProperties("variables")
                    public static class Variable {

                        private List<String> enums;

                        private String defaultValue;

                        private String description;

                        public List<String> getEnums() {
                            return enums;
                        }

                        public void setEnums(List<String> enums) {
                            this.enums = enums;
                        }

                        public String getDefaultValue() {
                            return defaultValue;
                        }

                        public void setDefaultValue(String defaultValue) {
                            this.defaultValue = defaultValue;
                        }

                        public String getDescription() {
                            return description;
                        }

                        public void setDescription(String description) {
                            this.description = description;
                        }
                    }
                }

                @ConfigurationProperties("components")
                public static class Components {

                    private Map<String, SecurityScheme> securitySchemes;

                    public Map<String, SecurityScheme> getSecuritySchemes() {
                        return securitySchemes;
                    }

                    public void setSecuritySchemes(Map<String, SecurityScheme> securitySchemes) {
                        this.securitySchemes = securitySchemes;
                    }

                    @ConfigurationProperties("securitySchemes")
                    public static class SecurityScheme {

                        private String type;

                        private String description;

                        private String name;

                        private org.babyfish.jimmer.client.generator.openapi.OpenApiProperties.In
                                in =
                                        org.babyfish.jimmer.client.generator.openapi
                                                .OpenApiProperties.In.HEADER;

                        private String scheme;

                        private String bearerFormat;

                        private Flows flows = new Flows();

                        private String openIdConnectUrl;

                        public String getType() {
                            return type;
                        }

                        public void setType(String type) {
                            this.type = type;
                        }

                        public String getDescription() {
                            return description;
                        }

                        public void setDescription(String description) {
                            this.description = description;
                        }

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }

                        public org.babyfish.jimmer.client.generator.openapi.OpenApiProperties.In
                                getIn() {
                            return in;
                        }

                        public void setIn(
                                org.babyfish.jimmer.client.generator.openapi.OpenApiProperties.In
                                        in) {
                            this.in = in;
                        }

                        public String getScheme() {
                            return scheme;
                        }

                        public void setScheme(String scheme) {
                            this.scheme = scheme;
                        }

                        public String getBearerFormat() {
                            return bearerFormat;
                        }

                        public void setBearerFormat(String bearerFormat) {
                            this.bearerFormat = bearerFormat;
                        }

                        public Flows getFlows() {
                            return flows;
                        }

                        public void setFlows(Flows flows) {
                            this.flows = flows;
                        }

                        public String getOpenIdConnectUrl() {
                            return openIdConnectUrl;
                        }

                        public void setOpenIdConnectUrl(String openIdConnectUrl) {
                            this.openIdConnectUrl = openIdConnectUrl;
                        }

                        @ConfigurationProperties("flows")
                        public static class Flows {

                            private Flow implicit = new Flow();

                            private Flow password = new Flow();

                            private Flow clientCredentials = new Flow();

                            private Flow authorizationCode = new Flow();

                            public Flow getImplicit() {
                                return implicit;
                            }

                            public void setImplicit(Flow implicit) {
                                this.implicit = implicit;
                            }

                            public Flow getPassword() {
                                return password;
                            }

                            public void setPassword(Flow password) {
                                this.password = password;
                            }

                            public Flow getClientCredentials() {
                                return clientCredentials;
                            }

                            public void setClientCredentials(Flow clientCredentials) {
                                this.clientCredentials = clientCredentials;
                            }

                            public Flow getAuthorizationCode() {
                                return authorizationCode;
                            }

                            public void setAuthorizationCode(Flow authorizationCode) {
                                this.authorizationCode = authorizationCode;
                            }

                            @ConfigurationProperties("flow")
                            public static class Flow {

                                private String authorizationUrl;

                                private String tokenUrl;

                                private String refreshUrl;

                                private Map<String, String> scopes;

                                public String getAuthorizationUrl() {
                                    return authorizationUrl;
                                }

                                public void setAuthorizationUrl(String authorizationUrl) {
                                    this.authorizationUrl = authorizationUrl;
                                }

                                public String getTokenUrl() {
                                    return tokenUrl;
                                }

                                public void setTokenUrl(String tokenUrl) {
                                    this.tokenUrl = tokenUrl;
                                }

                                public String getRefreshUrl() {
                                    return refreshUrl;
                                }

                                public void setRefreshUrl(String refreshUrl) {
                                    this.refreshUrl = refreshUrl;
                                }

                                public Map<String, String> getScopes() {
                                    return scopes;
                                }

                                public void setScopes(Map<String, String> scopes) {
                                    this.scopes = scopes;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Singleton
    public static class StringToDialectConverter implements TypeConverter<String, Dialect> {
        @Override
        public Optional<Dialect> convert(
                String value, Class<Dialect> targetType, ConversionContext context) {
            Dialect dialect;
            Class<?> clazz;
            try {
                clazz = Class.forName(value, true, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException(
                        "The class \""
                                + value
                                + "\" specified by `micronaut.jimmer.dialect` cannot be found");
            }
            if (!Dialect.class.isAssignableFrom(clazz) || clazz.isInterface()) {
                throw new IllegalArgumentException(
                        "The class \""
                                + value
                                + "\" specified by `micronaut.jimmer.dialect` must be a valid dialect implementation");
            }
            try {
                dialect = (Dialect) clazz.getConstructor().newInstance();
            } catch (InvocationTargetException ex) {
                throw new IllegalArgumentException(
                        "Create create instance for the class \""
                                + value
                                + "\" specified by `micronaut.jimmer.dialect`",
                        ex.getTargetException());
            } catch (Exception ex) {
                throw new IllegalArgumentException(
                        "Create create instance for the class \""
                                + value
                                + "\" specified by `micronaut.jimmer.dialect`",
                        ex);
            }
            return Optional.of(dialect);
        }
    }

    @Singleton
    public static class MapToVariableConverter
            implements TypeConverter<
                    Map<String, Object>, Client.Openapi.OpenApiProperties.Server.Variable> {
        @Override
        public Optional<Client.Openapi.OpenApiProperties.Server.Variable> convert(
                Map<String, Object> map,
                Class<Client.Openapi.OpenApiProperties.Server.Variable> targetType,
                ConversionContext context) {
            Client.Openapi.OpenApiProperties.Server.Variable variable =
                    new Client.Openapi.OpenApiProperties.Server.Variable();
            variable.setEnums((List<String>) map.get("enums"));
            variable.setDefaultValue((String) map.get("defaultValue"));
            variable.setDescription((String) map.get("description"));
            return Optional.of(variable);
        }
    }

    @Singleton
    public static class MapToSecuritySchemeConverter
            implements TypeConverter<
                    Map<String, Object>,
                    Client.Openapi.OpenApiProperties.Components.SecurityScheme> {
        @Override
        public Optional<Client.Openapi.OpenApiProperties.Components.SecurityScheme> convert(
                Map<String, Object> map,
                Class<Client.Openapi.OpenApiProperties.Components.SecurityScheme> targetType,
                ConversionContext context) {
            Client.Openapi.OpenApiProperties.Components.SecurityScheme scheme =
                    new Client.Openapi.OpenApiProperties.Components.SecurityScheme();
            scheme.setType((String) map.get("type"));
            scheme.setDescription((String) map.get("description"));
            scheme.setName((String) map.get("name"));
            scheme.setScheme((String) map.get("scheme"));
            scheme.setBearerFormat((String) map.get("bearerFormat"));
            scheme.setOpenIdConnectUrl((String) map.get("openIdConnectUrl"));

            Object inObj = map.get("in");
            if (inObj instanceof String) {
                try {
                    scheme.setIn(
                            org.babyfish.jimmer.client.generator.openapi.OpenApiProperties.In
                                    .valueOf(((String) inObj).toUpperCase()));
                } catch (Exception ignore) {
                }
            }

            Object flowsObj = map.get("flows");
            if (flowsObj instanceof Map) {
                Client.Openapi.OpenApiProperties.Components.SecurityScheme.Flows flows =
                        new Client.Openapi.OpenApiProperties.Components.SecurityScheme.Flows();
                Map<String, Object> flowsMap = (Map<String, Object>) flowsObj;
                flows.setImplicit(parseFlow(flowsMap.get("implicit")));
                flows.setPassword(parseFlow(flowsMap.get("password")));
                flows.setClientCredentials(parseFlow(flowsMap.get("clientCredentials")));
                flows.setAuthorizationCode(parseFlow(flowsMap.get("authorizationCode")));
                scheme.setFlows(flows);
            }

            return Optional.of(scheme);
        }

        private Client.Openapi.OpenApiProperties.Components.SecurityScheme.Flows.Flow parseFlow(
                Object flowObj) {
            if (!(flowObj instanceof Map)) return null;
            Map<String, Object> flowMap = (Map<String, Object>) flowObj;
            Client.Openapi.OpenApiProperties.Components.SecurityScheme.Flows.Flow flow =
                    new Client.Openapi.OpenApiProperties.Components.SecurityScheme.Flows.Flow();
            flow.setAuthorizationUrl((String) flowMap.get("authorizationUrl"));
            flow.setTokenUrl((String) flowMap.get("tokenUrl"));
            flow.setRefreshUrl((String) flowMap.get("refreshUrl"));
            Object scopesObj = flowMap.get("scopes");
            if (scopesObj instanceof Map) {
                Map<?, ?> rawMap = (Map<?, ?>) scopesObj;
                Map<String, String> scopes = new java.util.LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    scopes.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                }
                flow.setScopes(scopes);
            }
            return flow;
        }
    }
}
