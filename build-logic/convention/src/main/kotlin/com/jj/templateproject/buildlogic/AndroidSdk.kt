package com.jj.templateproject.buildlogic

/**
 * The SDK levels every module compiles against, in one place.
 *
 * Shared by [configureKotlinAndroid] and [configureKotlinMultiplatform] so an Android-only module
 * and a multiplatform one cannot drift apart on the levels they target, which is the kind of
 * difference that only shows up as a strange lint or runtime failure in one module.
 */
internal const val COMPILE_SDK = 36

/** @see COMPILE_SDK */
internal const val MIN_SDK = 23
