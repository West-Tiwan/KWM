import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":bar"))
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "org.example.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "KWM"
            packageVersion = project.version.toString()
            description = "Tiling window manager"
            vendor = "Mohit Singh"
            modules("java.instrument", "java.management", "jdk.unsupported")

            macOS {
                bundleID = "com.westtiwan.kwm"
                // Background agent: no Dock icon or app menu
                infoPlist {
                    extraKeysRawXml = """
                        <key>LSUIElement</key>
                        <true/>
                    """.trimIndent()
                }
            }
        }
    }
}

// Compose can only build an .msi on Windows itself, so ship Windows as a plain zip that runs on an installed JDK
// Everything else is the same as on macOS; only Skia's native library differs (keep in step with Compose)
val windowsRuntime: Configuration by configurations.creating {
    isTransitive = false
}

dependencies {
    windowsRuntime("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.93")
}

val windowsZip by tasks.registering(Zip::class) {
    group = "distribution"
    description = "Packages KWM for Windows x64 (needs Java 17+)."
    archiveFileName.set("KWM-${project.version}-windows-x64.zip")
    destinationDirectory.set(layout.buildDirectory.dir("release"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    into("KWM/lib") {
        from(tasks.jar)
        from(configurations.runtimeClasspath) { exclude("*-macos-*", "*-linux-*") }
        from(windowsRuntime)
    }
    into("KWM") {
        from("src/dist/windows")
    }
}

kotlin {
    jvmToolchain(17)
}
