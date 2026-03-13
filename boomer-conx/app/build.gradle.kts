plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "ca.boomerconx.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "ca.boomerconx.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        resourceConfigurations += listOf("en", "fr-rCA")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation(project(":core:core-security"))
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-data"))
    implementation(project(":feature:feature-onboarding"))
    implementation(project(":feature:feature-home"))
    implementation(project(":feature:feature-backup"))
    implementation(project(":feature:feature-launcher"))
    implementation(project(":feature:feature-vault"))
    implementation(project(":feature:feature-ice"))
    implementation(project(":feature:feature-scam-shield"))
    implementation(project(":feature:feature-accessibility"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    debugImplementation(libs.compose.ui.tooling)
}
