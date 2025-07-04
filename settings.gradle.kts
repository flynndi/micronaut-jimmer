rootProject.name = "micronaut-jimmer"
include(
    "micronaut-jimmer",
    "benchmark",
    "integration-tests-java",
    "integration-tests-kotlin",
    "micronaut-jimmer-repository-processor",
    "micronaut-jimmer-repository-sourcegen-generator-java",
    "micronaut-jimmer-repository-sourcegen-generator-kotlin",
)
include("benchmark:springboot-application")
include("benchmark:micronaut-application")
include("benchmark:benchmark")
