package com.jj.templateproject.data.utils

import com.jj.templateproject.BuildConfig

fun getAboutVersionText(): String = "Revision: ${BuildConfig.currentRevisionHash}, " +
        "Build number: ${BuildConfig.ciBuildNumber}, Version: ${BuildConfig.VERSION_NAME}"