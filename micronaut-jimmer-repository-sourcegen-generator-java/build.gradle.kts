plugins {
    `spotless-convention`
    `maven-publish`
    `java-library`
    signing
}

group = project.group.toString()
version = project.version.toString()

dependencies {
    implementation(platform(libs.micronaut))
    // generator code
    implementation(libs.micronaut.sourcegen.bytecode.writer)
    implementation(libs.micronaut.sourcegen.model)
    implementation(libs.micronaut.sourcegen.generator)
    testImplementation(libs.kotlinpoet)
    testImplementation(libs.kotlinpoet.javapoet)
    testImplementation(libs.asm)
    testImplementation(libs.asm.util)
    testImplementation(libs.java.decompiler.engine)
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
    withSourcesJar()
    withJavadocJar()
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
            artifactId = "micronaut-jimmer-repository-sourcegen-generator-java"
            version = project.version.toString()
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
