plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "KWM"
include("core")
include("bar")
include("launcher")
include("launcher:src.main.kotlin.org.example")
include("launcher:src")