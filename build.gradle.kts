plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.composeCompiler).apply(false)
    alias(libs.plugins.detekt)

    val kotlinVersion = libs.versions.kotlin.get()
    kotlin("plugin.serialization") version kotlinVersion apply false
}

buildscript {
    dependencies {
        classpath(libs.google.services)
        // On the classpath so app/build.gradle.kts can apply it by id, but only when a
        // google-services.json is present. Both plugins hard-fail without one.
        classpath(libs.firebase.crashlytics.gradle)
    }
}

// Static analysis. A single root `detekt` task scans every module's Kotlin sources against a
// shared config + baseline. It is intentionally NOT wired into `check`/`build`, so the unit-test
// build stays fast; run it explicitly with `./gradlew detekt` (regenerate `./gradlew detektBaseline`).
detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline = file("$rootDir/config/detekt/baseline.xml")
    parallel = true
    source.setFrom(
        files(
            "app/src/main",
            "domain/src/main",
            "networking/src/main",
            "core/src/main",
            "design/src/main",
            "build-logic/convention/src/main",
        )
    )
}