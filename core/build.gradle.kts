plugins {
    kotlin("jvm")
}
dependencies {
    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("net.java.dev.jna:jna-platform:5.14.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.github.oshi:oshi-core:6.4.5")

    testImplementation(kotlin("test"))
}
kotlin {
    jvmToolchain(17)
}
tasks.test {
    useJUnitPlatform()
}