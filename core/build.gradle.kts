plugins {
    alias(libs.plugins.templateproject.android.library)
}

android {
    namespace = "com.jj.templateproject.core"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":networking"))
    implementation(libs.bolts.tasks)

    implementation(libs.koin)
    implementation(libs.coroutinesCore)
    // ProcessLifecycleOwner, for the process-wide foreground state behind AppLifecycle.
    implementation(libs.lifecycleProcess)

    testImplementation(libs.junit5)
    testImplementation(libs.robolectric)
    // Aligns the JUnit Platform launcher with junit-jupiter so test discovery works; without it the
    // test task fails to start.
    testRuntimeOnly(libs.junitPlatformLauncher)
    testRuntimeOnly(libs.junitVintageEngine)
    testImplementation(libs.junit4)
}
