subprojects {
    apply(plugin = "java")
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
    repositories {
        mavenCentral()
    }
}
