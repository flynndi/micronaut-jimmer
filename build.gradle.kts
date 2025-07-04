plugins {
    `spotless-convention`
    `nexusPublishing-convention`
}
allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://www.jetbrains.com/intellij-repository/releases")
        }
    }
}
