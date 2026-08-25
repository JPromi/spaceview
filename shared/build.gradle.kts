import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.buildkonfig)
    id("com.mikepenz.aboutlibraries.plugin")
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    androidLibrary {
       namespace = "com.jpromi.spaceview.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.icons.lucide)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinxJson)
            implementation(libs.kotlinx.datetime)
            implementation(libs.multiplatform.settings.noArg)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.coil.svg)
            implementation(libs.aboutlibraries.core)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

buildkonfig {
    packageName = "com.jpromi.spaceview"
    objectName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(STRING, "APP_VERSION", providers.gradleProperty("app.version").get(), const = true)
        buildConfigField(STRING, "ANDROID_VERSION", providers.gradleProperty("android.version").get(), const = true)
        buildConfigField(INT, "ANDROID_VERSION_CODE", providers.gradleProperty("android.versionCode").get(), const = true)
        buildConfigField(INT, "IOS_BUILD_NUMBER", providers.gradleProperty("ios.buildNumber").get(), const = true)
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}


aboutLibraries {
    export {
        // Define the output path for manual generation
        // Adjust the path based on your project structure (e.g., composeResources, Android res/raw)
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
        // Optionally specify the variant for export
        // variant = "release"
    }
}