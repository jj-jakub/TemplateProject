package com.jj.templateproject.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Shared configuration for every multiplatform module.
 *
 * The Android half is deliberately identical to [configureKotlinAndroid]'s (same compileSdk, minSdk
 * and Java level), so a module's Android build does not quietly differ depending on whether it has
 * been converted yet.
 */
internal fun Project.configureKotlinMultiplatform(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = COMPILE_SDK

        defaultConfig {
            minSdk = MIN_SDK
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        testOptions {
            unitTests.isIncludeAndroidResources = true
            // Deliberately NOT useJUnitPlatform() here, unlike the Android-only modules: shared tests
            // are written against kotlin.test, which maps to JUnit 4 on the Android target. Forcing
            // the JUnit 5 platform would need the vintage engine on every multiplatform module for
            // no gain, since a test that has to run on iOS as well cannot use JUnit 5 anyway.
        }
    }

    extensions.configure<KotlinMultiplatformExtension> {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        // Only the arm64 device and the Apple-Silicon simulator. iosX64 (the Intel-Mac simulator) is
        // left out on purpose: nothing in this fleet runs on an Intel Mac, and carrying its
        // dependency graph only slows sync down.
        iosArm64()
        iosSimulatorArm64()

        // Opting in once here rather than per file: every module's common code ends up touching one
        // of these eventually, and a per-file annotation is noise that hides the real ones.
        sourceSets.all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }
    }
}
