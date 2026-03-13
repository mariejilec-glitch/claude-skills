pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolution {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BoomerConX"

include(":app")
include(":core:core-common")
include(":core:core-security")
include(":core:core-ui")
include(":core:core-data")
include(":feature:feature-onboarding")
include(":feature:feature-home")
include(":feature:feature-backup")
include(":feature:feature-launcher")
include(":feature:feature-vault")
include(":feature:feature-ice")
include(":feature:feature-scam-shield")
include(":feature:feature-accessibility")
