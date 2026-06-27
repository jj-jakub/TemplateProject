package com.jj.templateproject.buildlogic

import com.android.build.api.dsl.CommonExtension

/**
 * Enables Jetpack Compose (and BuildConfig, which the Compose modules rely on) for a module.
 * The Compose compiler plugin itself is applied by the *.compose convention plugins.
 */
internal fun configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
            buildConfig = true
        }
    }
}
