plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

dependencies {
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "bar.BarAppKt"
    }
}

kotlin {
    jvmToolchain(17)
}
