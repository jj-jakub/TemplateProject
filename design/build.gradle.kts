plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.composeCompiler)
}

android {
    compileSdk = 34
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    namespace = "com.jj.templateproject.design"
}

dependencies {
    implementation(libs.composeUi)
    implementation(libs.composeMaterial3)
    implementation(libs.composeNavigation)
    implementation(libs.composePreview)
    implementation(libs.composeActivity)
}