import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.components.resources)
    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.jpromi.spaceview.MainKt"

        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SpaceView"
            packageVersion = "1.0.0"

            windows {
                iconFile.set(project.file("src/main/resources/icons/windows.ico"))
            }

            linux {
                iconFile.set(project.file("src/main/resources/icons/linux.png"))
            }

            // ToDo: MacOS App icon
        }
    }
}
