plugins {
    alias(libs.plugins.templateproject.kmp.library)
}

android {
    namespace = "com.jj.templateproject.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Pure-Kotlin coroutine primitives (CoroutineDispatcher, Dispatchers, Flow). An external
            // library rather than a project layer, so it does not breach the Konsist
            // `domain.dependsOnNothing()` rule, which only governs com.jj.templateproject.* layers.
            implementation(libs.coroutinesCore)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutinesTest)
        }
    }
}
