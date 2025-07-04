plugins {
    `spotless-convention`
    alias(libs.plugins.micronaut.application)
    alias(libs.plugins.micronaut.aot)
    `test-logging-conventions`
}

group = project.group.toString()
version = project.version.toString()

dependencies {
    implementation(platform(libs.micronaut))
    implementation(project(":micronaut-jimmer"))
    runtimeOnly(libs.snakeyaml)
    runtimeOnly(libs.h2)
    runtimeOnly(libs.logback.classic)
    implementation(libs.micronaut.jdbc.hikari)
    implementation(libs.micronaut.data.model)
    implementation(libs.micronaut.redis.lettuce)
    implementation(libs.micronaut.cache.caffeine)
    implementation(libs.redisson.micronaut)
    implementation(libs.micronaut.graphql)
    implementation(libs.graphql.java.extended.scalars)
    testImplementation(libs.micronaut.test.rest.assured)
    annotationProcessor(libs.jimmer.apt)
    annotationProcessor(project(":micronaut-jimmer-repository-processor"))
    annotationProcessor(project(":micronaut-jimmer-repository-sourcegen-generator-java"))
}

application {
    mainClass = "io.micronaut.jimmer.java.it.Application"
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("io.micronaut.jimmer.java.it.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}

tasks.withType<JavaCompile> {
    options.isIncremental = false
}
