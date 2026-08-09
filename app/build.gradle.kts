plugins {
    alias(libs.plugins.templateproject.android.application)
    alias(libs.plugins.templateproject.android.application.compose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sonarqube)
}

// Firebase is opt-in. The google-services plugin (which bakes google-services.json into the build
// so Firebase can initialize) is applied automatically only when you've added your own
// google-services.json to app/ — so the template still builds without one. After you add it,
// register your variant application IDs in the Firebase console (note the .fl1/.fl2 + .debug
// suffixes) and switch the Koin bindings in mainModule to the Firebase Analytics/Crash impls.
if (project.file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
    // Uploads R8's mapping file with every release build. Without it the line numbers kept by
    // proguard-rules.pro are only readable from the mapping archived by the release workflow, which
    // means reading a production stack trace by hand. Guarded exactly like the plugin above: with no
    // google-services.json there is no Firebase project to upload to, and the plugin hard-fails.
    apply(plugin = "com.google.firebase.crashlytics")
}

sonar {
    properties {
        property("sonar.projectKey", "jj-jakub_TemplateProject")
        property("sonar.organization", "jj-jakub")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

// Read the git short hash lazily via a provider so it is compatible with the
// Gradle configuration cache (exec {} at configuration time is not).
val gitShortHash = providers.exec {
    commandLine("git", "rev-parse", "--short=8", "HEAD")
    isIgnoreExitValue = true
}.standardOutput.asText.map { it.trim() }

val propertiesFile = rootProject.file("local.properties")
val properties = `java.util`.Properties()
if (propertiesFile.exists()) {
    propertiesFile.inputStream().use(properties::load)
}

val ciBuildNumber = properties["ciBuildNumber"] ?: 0

// Release signing is opt-in through the environment, so a fresh clone still builds: with no keystore
// configured the release build simply comes out unsigned (AGP names its output
// ...-release-unsigned.apk) instead of failing. CI decodes the keystore into the workspace and
// exports these four (see .github/actions/decode-keystore); locally, point SIGNING_STORE_FILE at
// your own keystore. Read through `providers` so the configuration cache tracks them as build
// inputs rather than baking one run's environment into a reused configuration.
val signingStoreFile = providers.environmentVariable("SIGNING_STORE_FILE").orNull?.takeIf(String::isNotBlank)
val signingStorePassword = providers.environmentVariable("SIGNING_STORE_PASSWORD").orNull
val signingKeyAlias = providers.environmentVariable("SIGNING_KEY_ALIAS").orNull
val signingKeyPassword = providers.environmentVariable("SIGNING_KEY_PASSWORD").orNull
val hasReleaseSigningConfig = signingStoreFile != null && file(signingStoreFile).exists()

android {
    // compileSdk (36), minSdk (23), targetSdk (35), Java 17, JUnit5 and Compose are
    // configured by the templateproject.android.application[.compose] convention plugins.
    defaultConfig {
        applicationId = "com.jj.templateproject"
        versionCode = 1
        versionName = "0.1"

        buildConfigField("String", "currentRevisionHash", "\"${gitShortHash.get()}\"")
        buildConfigField("int", "ciBuildNumber", "$ciBuildNumber")
        testInstrumentationRunner = "com.jj.templateproject.HermeticTestRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
    if (hasReleaseSigningConfig) {
        signingConfigs {
            create("release") {
                storeFile = file(signingStoreFile!!)
                storePassword = signingStorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false

            buildConfigField("String", "ServerBaseUrl", "\"https://www.google.com\"")
            buildConfigField(
                "String",
                "adMainBannerViewAdUnitId",
                "\"ca-app-pub-3940256099942544/6300978111\""
            )
            buildConfigField(
                "String",
                "adInterstitialAdUnitId",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )
            manifestPlaceholders["admobId"] = "ca-app-pub-7809340407306359~6994189425"
            applicationIdSuffix = ".debug"
        }

        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasReleaseSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }

            buildConfigField("String", "licensingBase64PublicKey", "\"\"")
            buildConfigField("String", "ServerBaseUrl", "\"https://www.google.com\"")
            buildConfigField(
                "String",
                "adMainBannerViewAdUnitId",
                "\"ca-app-pub-3940256099942544/6300978111\""
            )
            buildConfigField(
                "String",
                "adInterstitialAdUnitId",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )
            manifestPlaceholders["admobId"] = "ca-app-pub-7809340407306359~6994189425"
        }
    }

    flavorDimensions.add("version")
    productFlavors {
        create("flavor1") {
            applicationIdSuffix = ".fl1"
        }
        create("flavor2") {
            applicationIdSuffix = ".fl2"
        }
    }

    namespace = "com.jj.templateproject"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":design"))
    implementation(project(":networking"))

    implementation(libs.kotlinStdlib)
    implementation(libs.koin)
    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.lifecycleViewModelKtx)
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.lifecycleLiveData)
    implementation(libs.navigationKtx)
    implementation(libs.koinCompose)
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseAnalytics)
    implementation(libs.firebaseMessaging)
    implementation(libs.firebaseCrashlytics)
    implementation(libs.firebaseConfig)
    implementation(libs.accompanistPermissions)

    implementation(libs.googleAds)

    implementation(libs.composeUi)
    implementation(libs.composeMaterial3)
    // The bottom navigation bar's Icons.Default.Home/MailOutline/Settings. material3 carried
    // material-icons-core as an api dependency until 1.4.0 and no longer does, so the icon set is
    // now asked for by name instead of inherited.
    implementation(libs.materialIconsCore)
    implementation(libs.composeNavigation)
    implementation(libs.composePreview)
    implementation(libs.composeActivity)

    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit4)
    testImplementation(libs.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.koinTest)
    testImplementation(libs.koinTestJUnit5)
    testImplementation(libs.androidxNavTesting)
    testImplementation(libs.robolectric)
    testImplementation(libs.ui.test.junit4.android)
    testImplementation(libs.turbine)
    testImplementation(libs.mockwebserver)
    testRuntimeOnly(libs.junitVintageEngine)
    // Aligns the JUnit Platform launcher with junit-jupiter 5.13 so test discovery works
    // (Gradle's bundled launcher is older). Without it the test task fails to start.
    testRuntimeOnly(libs.junitPlatformLauncher)

    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.konsist)

    androidTestImplementation(libs.mockkAndroid)
    androidTestImplementation(libs.androidTestRules)
    androidTestImplementation(libs.uiAutomator)
    androidTestImplementation(libs.ui.test.junit4.android)
    // Provides the ComponentActivity used by createAndroidComposeRule in instrumented tests.
    debugImplementation(libs.ui.test.manifest)
}