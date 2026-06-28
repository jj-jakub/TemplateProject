plugins {
    alias(libs.plugins.templateproject.android.library)
}

android {
    namespace = "com.jj.templateproject.domain"
}

dependencies {
    // Pure-Kotlin coroutine primitives (CoroutineDispatcher, Dispatchers) for DispatcherProvider.
    // This is an external library, not a project layer, so it does not breach the Konsist
    // `domain.dependsOnNothing()` rule (which only governs com.jj.templateproject.* layers).
    implementation(libs.coroutinesCore)

    testImplementation(libs.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
