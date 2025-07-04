plugins {
    id("java")
    id("java-library")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    api(libs.spring.boot.starter) {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }
    api(libs.jimmer.spring.boot.starter)
    runtimeOnly(libs.h2)
    annotationProcessor(libs.jimmer.apt)
}

tasks.test {
    useJUnitPlatform()
}
