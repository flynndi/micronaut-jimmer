import io.github.gradlenexus.publishplugin.NexusPublishExtension

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

extensions.configure<NexusPublishExtension> {
    repositories {
        sonatype {
            username.set(findProperty("OSSRH_USERNAME") as String?)
            password.set(findProperty("OSSRH_PASSWORD") as String?)
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}
