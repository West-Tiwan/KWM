plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
}

compose.desktop {
    application {
        mainClass = "bar.BarAppKt"
    }
}

kotlin {
    jvmToolchain(17)
}
