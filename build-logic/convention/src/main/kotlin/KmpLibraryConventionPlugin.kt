import com.android.build.api.dsl.LibraryExtension
import com.jj.templateproject.buildlogic.configureKotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * A library shared by Android and iOS. The multiplatform counterpart of
 * [AndroidLibraryConventionPlugin]; a module applies one or the other, never both.
 */
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.multiplatform")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinMultiplatform(this)
            }
        }
    }
}
