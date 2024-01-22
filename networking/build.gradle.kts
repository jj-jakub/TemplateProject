plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.jj.templateproject.networking"
    compileSdk = 34
    defaultConfig {
        minSdk = 23
        targetSdk = 34
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
}

dependencies {
    implementation(project(":domain"))

    api(libs.retrofit)
    api(libs.retrofitCoroutines)
    api(libs.retrofitConverter)
    api(libs.okhttpInterceptor)

    implementation(libs.koin)
}