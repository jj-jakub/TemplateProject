plugins {
    alias(libs.plugins.templateproject.android.library)
}

android {
    namespace = "com.jj.templateproject.domain"
}

dependencies {
    testImplementation(libs.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
