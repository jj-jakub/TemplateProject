plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = ConfigData.compileSdk
    defaultConfig {
        minSdk = ConfigData.minSdk
        targetSdk = ConfigData.targetSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = ConfigData.kotlinJvmTarget
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    namespace = "com.jj.templateproject.core"
}

dependencies {
    implementation(project (":domain"))
    implementation(project (":networking"))

    implementation(Dependencies.koin)
}