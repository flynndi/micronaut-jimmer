import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("com.diffplug.spotless")
}

extensions.configure<SpotlessExtension> {
    java {
        target("src/**/*.java")
        removeUnusedImports()
        googleJavaFormat().aosp()
    }
    kotlin {
        target("src/**/*.kt")
        ktlint("1.6.0")
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.6.0")
    }
}

tasks.matching { it.name == "build" }.configureEach {
    dependsOn("spotlessApply")
}
