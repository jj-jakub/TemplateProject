import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {

    const val kotlinStdlib = ("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

    const val koin = ("io.insert-koin:koin-android:${Versions.koin}")

    const val coroutinesCore =
        ("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
    const val coroutinesAndroid =
        ("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}")
    const val lifecycleViewModelKtx =
        ("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")
    const val lifecycleRuntimeKtx =
        ("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")
    const val lifecycleLiveData =
        ("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}")
    const val accompanistSystemUiController =
        ("com.google.accompanist:accompanist-systemuicontroller:${Versions.system_ui_controller}")
    const val navigationKtx = ("androidx.navigation:navigation-ui-ktx:${Versions.navigationKtx}")

    const val composeUi = ("androidx.compose.ui:ui:${Versions.composeUi}")
    const val composeMaterial3 =
        ("androidx.compose.material3:material3:${Versions.composeMaterial3}")
    const val composeNavigation =
        ("androidx.navigation:navigation-compose:${Versions.composeNavigation}")
    const val composePreview = ("androidx.compose.ui:ui-tooling:${Versions.composeUi}")
    const val composeActivity = ("androidx.activity:activity-compose:${Versions.composeActivity}")
    const val koinCompose = ("io.insert-koin:koin-androidx-compose:${Versions.composeKoin}")

    const val firebaseBom = ("com.google.firebase:firebase-bom:${Versions.firebaseBom}")
    const val firebaseAnalytics = ("com.google.firebase:firebase-analytics-ktx")

    const val junit5 = ("org.junit.jupiter:junit-jupiter:${Versions.junit5}")
    const val mockk = ("io.mockk:mockk:${Versions.mockk}")
    const val coroutinesTest =
        ("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}")

    const val mockkAndroid = ("io.mockk:mockk-android:${Versions.mockkAndroid}")
    const val junitAndroid = ("androidx.test.ext:junit:${Versions.junitAndroid}")
    const val espressoAndroid = ("androidx.test.espresso:espresso-core:${Versions.espressoAndroid}")
    const val androidTestRunner = ("androidx.test:runner:${Versions.androidTestRunner}")
    const val androidTestRules = ("androidx.test:rules:${Versions.androidTestRules}")
    const val uiAutomator = ("androidx.test.uiautomator:uiautomator:${Versions.uiAutomator}")

    const val retrofit = ("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    const val retrofitCoroutines =
        ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.retrofitCoroutines}")
    const val retrofitConverter =
        ("com.squareup.retrofit2:converter-gson:${Versions.retrofitConverter}")
    const val okhttpInterceptor =
        ("com.squareup.okhttp3:logging-interceptor:${Versions.okhttpInterceptor}")
}

fun DependencyHandler.commonDependencies() {
    implementation(Dependencies.kotlinStdlib)
    implementation(Dependencies.koin)
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.coroutinesAndroid)
    implementation(Dependencies.lifecycleViewModelKtx)
    implementation(Dependencies.lifecycleRuntimeKtx)
    implementation(Dependencies.lifecycleLiveData)
    implementation(Dependencies.navigationKtx)
    implementation(Dependencies.koinCompose)
    implementationPlatform(Dependencies.firebaseBom)
    implementation(Dependencies.firebaseAnalytics)
}

fun DependencyHandler.composeDependencies() {
    implementation(Dependencies.composeUi)
    implementation(Dependencies.composeMaterial3)
    implementation(Dependencies.composeNavigation)
    implementation(Dependencies.composePreview)
    implementation(Dependencies.composeActivity)
    implementation(Dependencies.accompanistSystemUiController)
}

fun DependencyHandler.retrofitDependencies() {
    api(Dependencies.retrofit)
    api(Dependencies.retrofitCoroutines)
    api(Dependencies.retrofitConverter)
    api(Dependencies.okhttpInterceptor)
}

fun DependencyHandler.testDependencies() {
    testImplementation(Dependencies.junit5)
    testImplementation(Dependencies.mockk)
    testImplementation(Dependencies.coroutinesTest)
}

fun DependencyHandler.androidTestDependencies() {
    androidTestImplementation(Dependencies.mockkAndroid)
    androidTestImplementation(Dependencies.junitAndroid)
    androidTestImplementation(Dependencies.espressoAndroid)
    androidTestImplementation(Dependencies.androidTestRunner)
    androidTestImplementation(Dependencies.androidTestRules)
    androidTestImplementation(Dependencies.uiAutomator)
}

fun DependencyHandler.implementation(depName: String) {
    add("implementation", depName)
}

fun DependencyHandler.implementationPlatform(depName: String) {
    add("implementation", platform(depName))
}

private fun DependencyHandler.api(depName: String) {
    add("api", depName)
}

fun DependencyHandler.testImplementation(depName: String) {
    add("testImplementation", depName)
}

fun DependencyHandler.androidTestImplementation(depName: String) {
    add("androidTestImplementation", depName)
}