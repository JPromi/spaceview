plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
}

tasks.register("syncIosVersion") {
    val appVersion = providers.gradleProperty("app.version")
    val iosBuildNumber = providers.gradleProperty("ios.buildNumber")
    val configFile = layout.projectDirectory.file("iosApp/Configuration/Config.xcconfig")

    inputs.property("appVersion", appVersion)
    inputs.property("iosBuildNumber", iosBuildNumber)
    outputs.file(configFile)

    doLast {
        val file = configFile.asFile
        val current = file.readText()
        val updated = current
            .replace(Regex("""(?m)^CURRENT_PROJECT_VERSION=.*$"""), "CURRENT_PROJECT_VERSION=${iosBuildNumber.get()}")
            .replace(Regex("""(?m)^MARKETING_VERSION=.*$"""), "MARKETING_VERSION=${appVersion.get()}")

        if (updated != current) {
            file.writeText(updated)
        }
    }
}
