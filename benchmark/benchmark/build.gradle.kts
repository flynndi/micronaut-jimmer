plugins {
    `spotless-convention`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(project(":benchmark:springboot-application"))
    implementation(project(":benchmark:micronaut-application"))
    implementation("org.openjdk.jmh:jmh-core:1.37")
    annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    annotationProcessor(libs.jimmer.apt)
}
