## Micronaut Jimmer

# Quick Start
## Dependency
Gradle Java Project
```kotlin
implementation("io.github.flynndi:micronaut-jimmer:0.0.1.CR2")
annotationProcessor("org.babyfish.jimmer:jimmer-apt:0.9.99")
annotationProcessor("io.github.flynndi:micronaut-jimmer-repository-processor:0.0.1.CR2")
annotationProcessor("io.github.flynndi:micronaut-jimmer-repository-sourcegen-generator-java:0.0.1.CR2")
```
Gradle Kotlin Project
```kotlin
implementation("io.github.flynndi:micronaut-jimmer:0.0.1.CR2")
ksp("org.babyfish.jimmer:jimmer-ksp:0.9.99")
ksp("io.github.flynndi:micronaut-jimmer-repository-processor:0.0.1.CR2")
ksp("io.github.flynndi:micronaut-jimmer-repository-sourcegen-generator-kotlin:0.0.1.CR2")
```
