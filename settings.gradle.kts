rootProject.name = "TemplateProject"
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
include(":app")
include(":networking")
include(":domain")
include(":design")
include(":core")
include(":lvl_library")