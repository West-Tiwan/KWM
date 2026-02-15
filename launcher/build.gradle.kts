plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":core"))
    implementation(project(":bar"))
}

application {
    mainClass.set("org.example.MainKt")
}

kotlin {
    jvmToolchain(17)
}
