package com.jj.templateproject.buildlogic

import com.android.build.api.dsl.CommonExtension

/**
 * Enables Jetpack Compose (and BuildConfig, which the Compose modules rely on) for a module.
 * The Compose compiler plugin itself is applied by the *.compose convention plugins.
 */
internal fun configureAndroidCompose(
    // AGP 9's new public DSL made CommonExtension non-generic (it used to carry six type
    // parameters for the build-type/product-flavor/variant family); this signature tracks that.
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        // AGP 9's CommonExtension dropped the buildFeatures { } block-lambda in favor of the plain
        // property getter; configure it via .apply on that property instead.
        buildFeatures.apply {
            compose = true
            buildConfig = true
        }
    }
}
