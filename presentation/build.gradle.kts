plugins {
    alias(libs.plugins.templateproject.kmp.library.compose)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.jj.templateproject.presentation"
}

kotlin {
    // The framework the iosApp Xcode project embeds (see iosApp/project.yml's
    // embedAndSignAppleFrameworkForXcode step). Not `export()`-ing :domain/:networking/:core/
    // :design here: Swift only ever calls the one entry point this module itself declares
    // (MainViewController(), in iosMain), never a type from those modules directly, so there is
    // nothing further that needs to be part of the Swift-visible API surface.
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Presentation"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":networking"))
            implementation(project(":core"))
            implementation(project(":design"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(libs.koinCore)
            implementation(libs.koinComposeViewmodel)
            implementation(libs.jb.navigation.compose)
            implementation(libs.jb.lifecycle.viewmodel)
            implementation(libs.jb.lifecycle.viewmodel.compose)
            implementation(libs.jb.lifecycle.runtime.compose)
            implementation(libs.jb.lifecycle.savedstate)
            implementation(libs.kotlinx.serialization.json)
        }
        androidMain.dependencies {
            // The Android runtime-permission ask (RequestNotificationPermissionOnLaunch's actual)
            // and the AdMob banner (ComposeAdView's actual) are both Android-only capabilities with
            // no Compose Multiplatform equivalent, so their dependencies stay androidMain-only even
            // though the composables that declare them are commonMain expect declarations.
            implementation(libs.accompanistPermissions)
            implementation(libs.googleAds)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutinesTest)
            implementation(libs.turbine)
        }
        val androidUnitTest by getting {
            dependencies {
                // A few Android navigation/lifecycle types (NavBackStackEntry, SavedStateHandle)
                // are mocked directly rather than faked by hand: they are platform framework
                // types with no constructor a fake could stand in for cleanly.
                implementation(libs.mockk)
            }
        }
    }
}

compose.resources {
    // A dedicated package rather than inheriting the module's own, so the generated Res.string.*
    // accessor reads the same regardless of which package a given screen file happens to live in.
    packageOfResClass = "com.jj.templateproject.presentation.generated.resources"
}
