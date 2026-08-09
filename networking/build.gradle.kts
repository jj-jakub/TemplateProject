plugins {
    alias(libs.plugins.templateproject.kmp.library)
}

android {
    namespace = "com.jj.templateproject.networking"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))

            api(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinxJson)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlinx.io.core)

            implementation(libs.koinCore)
            implementation(libs.coroutinesCore)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutinesTest)
            implementation(libs.ktor.client.mock)
        }
        // MockWebServer is JVM-only, which is exactly why this integration test lives here rather
        // than in commonTest: it is the one test that runs the real OkHttp engine end to end
        // (retry included) instead of MockEngine, so it is worth keeping even though it cannot
        // run on iOS.
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.mockwebserver)
            }
        }
    }
}

// mockwebserver:4.12.0 is compiled against OkHttp 4.x's internals, but ktor-client-okhttp pulls
// OkHttp 5.3.2 transitively, and Gradle's default "highest version wins" resolution applies that
// to mockwebserver too — its bytecode then references a class OkHttp 5.x removed
// (okhttp3.internal.Util), which fails at test runtime with NoClassDefFoundError rather than at
// compile time. Forced back to 4.12.0, scoped to the unit test classpaths only: the production
// app keeps whatever ktor-client-okhttp actually wants, since nothing there pairs it with
// mockwebserver.
configurations.matching { it.name.contains("UnitTest", ignoreCase = false) }.configureEach {
    resolutionStrategy {
        force("com.squareup.okhttp3:okhttp:${libs.versions.okhttpInterceptor.get()}")
    }
}
