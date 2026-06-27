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
}
