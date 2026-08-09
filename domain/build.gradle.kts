plugins {
    alias(libs.plugins.templateproject.kmp.library)
    alias(libs.plugins.kotlinSerialization)
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
            // For @Serializable on SavedGameState: a genuinely multiplatform, pure-Kotlin library,
            // the same exception coroutines-core already gets. The actual Json encode/decode calls
            // live in :core's DefaultGameStateStorage, not here — this module only needs the
            // annotation + serializer-generation machinery, not a JSON implementation.
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutinesTest)
        }
    }
}
