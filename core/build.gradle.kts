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
}
