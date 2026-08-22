plugins {
    alias(libs.plugins.templateproject.kmp.library)
}

android {
    namespace = "com.jj.templateproject.core"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))

            // koin-core, not koin-android: the Android artifact is a JVM one and cannot be seen from
            // commonMain. Only `platformCoreModule`'s Android actual needs `androidContext()`, and
            // that lives in androidMain, which is exactly what let this module split in two.
            implementation(libs.koinCore)
            implementation(libs.coroutinesCore)
        }
        androidMain.dependencies {
            implementation(libs.koin)
            implementation(libs.bolts.tasks)
            // ProcessLifecycleOwner, for the process-wide foreground state behind AppLifecycle.
            implementation(libs.lifecycleProcess)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        // Robolectric is a JVM/Android test runtime, so the device-class test it drives cannot move
        // to commonTest however platform-neutral its subject looks.
        getByName("androidUnitTest") {
            dependencies {
                implementation(libs.junit4)
                implementation(libs.robolectric)
            }
        }
    }
}
