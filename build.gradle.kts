plugins {
    kotlin("jvm") version "1.9.22" apply false
    id("org.jetbrains.compose") version "1.6.0" apply false
}

allprojects {
    group = "com.west-tiwan.kwm"
    version = "0.1.0"

    repositories {
        google()
        mavenCentral()
    }
}