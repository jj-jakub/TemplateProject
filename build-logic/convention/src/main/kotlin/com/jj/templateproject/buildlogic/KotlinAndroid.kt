package com.jj.templateproject.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Shared `android { }` configuration applied to every module via the convention plugins.
 * This replaces the compileSdk / minSdk / Java 17 / JUnit5 blocks that were previously
 * duplicated in each module's build.gradle.kts.
 */
internal fun Project.configureKotlinAndroid(
    // AGP 9's new public DSL made CommonExtension non-generic (it used to carry six type
    // parameters for the build-type/product-flavor/variant family); this signature tracks that.
    commonExtension: CommonExtension,
) {
    // AGP 9's CommonExtension dropped the defaultConfig/compileOptions/testOptions block-lambdas in
    // favor of plain property getters; configure each via .apply on that property instead.
    commonExtension.apply {
        compileSdk = COMPILE_SDK

        defaultConfig.apply {
            minSdk = MIN_SDK
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        testOptions.apply {
            unitTests.isIncludeAndroidResources = true
            unitTests.all {
                it.useJUnitPlatform()
            }
        }
    }

    extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}
