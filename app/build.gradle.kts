plugins {
    id("com.android.application")
    kotlin("android")
}

fun getCurrentRevisionHash(): String {
    val stdout = `java.io`.ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().substring(0, 8)
}

val propertiesFile = rootProject.file("./local.properties")
val properties = `java.util`.Properties()

try {
    properties.load(`java.io`.FileInputStream(propertiesFile))
} catch (e: Exception) {
    println("Exception occurred while loading propertiesFile, $e")
}

val ciBuildNumber = properties["ciBuildNumber"] ?: 0

android {
    compileSdk = ConfigData.compileSdk
    buildToolsVersion = ConfigData.buildTools

    defaultConfig {
        applicationId = "com.jj.templateproject"
        minSdk = ConfigData.minSdk
        targetSdk = ConfigData.targetSdk
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName

        buildConfigField("String", "currentRevisionHash", "\"${getCurrentRevisionHash()}\"")
        buildConfigField("int", "ciBuildNumber", "$ciBuildNumber")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystoreFile =
                file(System.getProperty("user.home") + "/work/_temp/keystore/TemplateProject.jks")
            storeFile = file(keystoreFile.path)
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false

            buildConfigField("String", "ServerBaseUrl", "\"https://www.google.com\"")
            buildConfigField("String", "adMainBannerViewAdUnitId", "\"ca-app-pub-3940256099942544/6300978111\"")
            buildConfigField("String", "adInterstitialAdUnitId", "\"ca-app-pub-3940256099942544/1033173712\"")
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
            signingConfig = signingConfigs.getByName("release")

            buildConfigField("String", "ServerBaseUrl", "\"https://www.google.com\"")
            buildConfigField("String", "adMainBannerViewAdUnitId", "\"ca-app-pub-3940256099942544/6300978111\"")
            buildConfigField("String", "adInterstitialAdUnitId", "\"ca-app-pub-3940256099942544/1033173712\"")
            manifestPlaceholders["admobId"] = "ca-app-pub-7809340407306359~6994189425"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = ConfigData.kotlinJvmTarget
    }
    buildFeatures {
        viewBinding = true
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ConfigData.kotlinCompilerExtension
    }
    namespace = "com.jj.templateproject"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":design"))
    implementation(project(":networking"))

    commonDependencies()
    adsDependencies()
    composeDependencies()
    testDependencies()
    androidTestDependencies()
}