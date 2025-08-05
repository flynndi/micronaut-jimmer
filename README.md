## Micronaut Jimmer

# Quick Start
## Dependency
Gradle Java Project
```kotlin
implementation("io.github.flynndi:micronaut-jimmer:0.0.1.CR4")
annotationProcessor("org.babyfish.jimmer:jimmer-apt:0.9.104")
annotationProcessor("io.github.flynndi:micronaut-jimmer-repository-processor:0.0.1.CR4")
annotationProcessor("io.github.flynndi:micronaut-jimmer-repository-sourcegen-generator-java:0.0.1.CR4")
```
Gradle Kotlin Project
```kotlin
implementation("io.github.flynndi:micronaut-jimmer:0.0.1.CR4")
ksp("org.babyfish.jimmer:jimmer-ksp:0.9.104")
ksp("io.github.flynndi:micronaut-jimmer-repository-processor:0.0.1.CR4")
ksp("io.github.flynndi:micronaut-jimmer-repository-sourcegen-generator-kotlin:0.0.1.CR4")
```
