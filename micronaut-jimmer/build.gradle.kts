plugins {
    `spotless-convention`
    `maven-publish`
    `java-library`
    signing
    kotlin("jvm")
    `test-logging-conventions`
}

group = project.group.toString()
version = project.version.toString()

dependencies {
    implementation(platform(libs.micronaut))
    api(kotlin("stdlib"))
    // datasource
    api(libs.micronaut.data.connection)
    // transaction management
    api(libs.micronaut.data.tx.jdbc)
    // jackson
    api(libs.micronaut.jackson.databind)
    // data model
    implementation(libs.micronaut.data.model)
    // redis
    implementation(libs.micronaut.redis.lettuce)
    // caffeine
    implementation(libs.micronaut.cache.caffeine)
    // graphql
    implementation(libs.micronaut.graphql)
    // http server
    implementation(libs.micronaut.http.client)
    // test
    testImplementation(libs.micronaut.test.junit5)
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("io.micronaut:micronaut-http-server-netty")
    // jimmer
    api(libs.jimmer.sql)
    api(libs.jimmer.sql.kotlin)
    api(libs.jimmer.client)
    api(libs.jimmer.client.swagger)
    annotationProcessor(libs.micronaut.inject.java)
    testAnnotationProcessor(libs.micronaut.inject.java)
    testImplementation("org.mockito:mockito-core")
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaCompile> {
    options.isIncremental = false
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            plugins.withType<JavaPlugin> {
                from(components["java"])
            }
            plugins.withType<JavaPlatformPlugin> {
                from(components["javaPlatform"])
            }
            pom {
                name.set("micronaut-jimmer")
                description.set("A revolutionary ORM framework for both java and kotlin, and a complete integrated solution")
                url.set("https://github.com/flynndi/micronaut-jimmer")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("flynndi")
                        name.set("flynndi")
                        email.set("lixuan0520@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:ssh://git@github.com/flynndi/micronaut-jimmer.git")
                    developerConnection.set("scm:git:ssh://git@github.com/flynndi/micronaut-jimmer.git")
                    url.set("https://github.com/flynndi/micronaut-jimmer")
                }
            }

            groupId = project.group.toString()
            artifactId = "micronaut-jimmer"
            version = project.version.toString()
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
