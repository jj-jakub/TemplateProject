plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 23
        targetSdk = 33
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

    namespace = "com.jj.templateproject.domain"
}