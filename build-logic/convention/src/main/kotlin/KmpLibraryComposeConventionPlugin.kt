import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Compose for a multiplatform library. The counterpart of [AndroidLibraryComposeConventionPlugin],
 * layered on [KmpLibraryConventionPlugin] the same way that one is layered on the Android-only base.
 *
 * Two plugins, because Compose is two separate things. `org.jetbrains.kotlin.plugin.compose` is the
 * *compiler* plugin that turns `@Composable` into runtime calls; it ships with Kotlin and is shared
 * with the Android-only Compose modules. `org.jetbrains.compose` is what supplies the multiplatform
 * Compose *artifacts* and the `compose.runtime` / `compose.foundation` / `compose.material3` /
 * `compose.ui` dependency accessors a module's `sourceSets` block then uses.
 *
 * Unlike [AndroidLibraryComposeConventionPlugin] this deliberately does not set AGP's
 * `buildFeatures.compose`: that flag exists so AGP can wire the Compose compiler itself, which is
 * exactly what `org.jetbrains.kotlin.plugin.compose` has already done here. Setting it would only
 * add a second, redundant path to the same result.
 */
class KmpLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("templateproject.kmp.library")
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("org.jetbrains.compose")
        }
    }
}
