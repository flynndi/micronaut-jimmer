plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.20")
    api("io.github.gradle-nexus:publish-plugin:2.0.0")
    api("com.diffplug.spotless:spotless-plugin-gradle:7.0.4")
    api("org.jetbrains.dokka:dokka-gradle-plugin:2.0.0")
}
