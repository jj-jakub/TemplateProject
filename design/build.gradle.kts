plugins {
    alias(libs.plugins.templateproject.android.library)
    alias(libs.plugins.templateproject.android.library.compose)
}

android {
    namespace = "com.jj.templateproject.design"
}

dependencies {
    implementation(libs.androidxCore)
    implementation(libs.composeUi)
    implementation(libs.composeMaterial3)
    implementation(libs.composeNavigation)
    implementation(libs.composePreview)
    implementation(libs.composeActivity)

    testImplementation(libs.junit5)
    // Robolectric + Compose UI test for device-free component tests (mirrors :app's setup).
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.ui.test.junit4.android)
    testRuntimeOnly(libs.junitVintageEngine)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
