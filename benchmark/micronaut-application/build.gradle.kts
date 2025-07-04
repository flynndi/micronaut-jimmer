plugins {
    id("application")
    id("java-library")
    `spotless-convention`
}

dependencies {
    api(platform(libs.micronaut))
    api(project(":micronaut-jimmer"))
    api("io.micronaut.data:micronaut-data-connection-jdbc")
    api("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-inject")
    runtimeOnly("com.h2database:h2")
    annotationProcessor(libs.jimmer.apt)
}

application {
    mainClass = "io.micronaut.jimmer.it.Application"
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}
