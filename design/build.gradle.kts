plugins {
    alias(libs.plugins.templateproject.kmp.library.compose)
}

android {
    namespace = "com.jj.templateproject.design"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // The Compose Multiplatform artifacts, not the androidx.compose ones. They publish
            // under the same androidx.compose.* package names, which is why every file in
            // commonMain kept its imports verbatim through the conversion; on the Android target
            // they resolve to the androidx artifacts anyway, so :app sees exactly what it saw
            // before.
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
        androidMain.dependencies {
            // ui-tooling, for @Preview. Android-only by nature, like the ThemePreviews annotation
            // and the ComponentCatalog gallery it serves.
            implementation(compose.uiTooling)
            // WindowCompat, for the status/navigation bar icon contrast in AdjustSystemBarAppearance.
            implementation(libs.androidxCore)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        // Robolectric-hosted Compose UI tests. There is no multiplatform equivalent: these drive a
        // real ComponentActivity through createAndroidComposeRule, so they are Android/JVM tests by
        // construction and stay here rather than being watered down into something that runs
        // everywhere. What they cover (that a component renders and reacts to a click) is the
        // Android half of the design system's contract; the palette, type scale, shape scale and
        // grid math are covered by commonTest and run on both platforms.
        getByName("androidUnitTest") {
            dependencies {
                implementation(libs.junit4)
                implementation(libs.robolectric)
                implementation(libs.ui.test.junit4.android)
            }
        }
    }
}
