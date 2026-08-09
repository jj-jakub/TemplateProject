import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.jj.templateproject.buildlogic"

// The convention plugins themselves compile/run on the JDK that runs Gradle; pin to 17
// to match the project toolchain.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // Provided by the consuming build's classpath at runtime (declared `apply false`
    // in the root build.gradle.kts); needed here only to compile against their DSL.
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "templateproject.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "templateproject.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "templateproject.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "templateproject.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("kmpLibrary") {
            id = "templateproject.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
    }
}
