plugins {
    alias(libs.plugins.templateproject.android.library)
}

android {
    namespace = "com.jj.templateproject.networking"
}

dependencies {
    implementation(project(":domain"))

    api(libs.retrofit)
    api(libs.retrofitConverter)
    api(libs.okhttpInterceptor)

    implementation(libs.koin)
}
