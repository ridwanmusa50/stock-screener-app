plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.dagger.hilt.android)
    kotlin("plugin.serialization") version "2.1.21"
}

android {
    namespace = "io.rid.stockscreenerapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.rid.stockscreenerapp"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // =============================================================================================================
    // Android + Compose UI
    // =============================================================================================================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.constraintlayout.compose)

    // =============================================================================================================
    // Kotlin Coroutines
    // =============================================================================================================
    implementation(libs.jetbrains.kotlinx.coroutines.android)
    implementation(libs.jetbrains.kotlinx.coroutines.core)

    // =============================================================================================================
    // Serialization
    // =============================================================================================================
    implementation(libs.jetbrains.kotlinx.serialization.json)

    // =============================================================================================================
    // Hilt - Dependency Injection
    // =============================================================================================================
    implementation(libs.androidx.hilt.hilt.navigation.compose)
    implementation(libs.androidx.hilt.hilt.navigation.fragment)
    implementation(libs.google.dagger.hilt.android)
    ksp(libs.google.dagger.hilt.compiler)

    // =============================================================================================================
    // Testing
    // =============================================================================================================
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // =============================================================================================================
    // Other Libraries
    // =============================================================================================================

    // Coil - Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

}

hilt { enableAggregatingTask = false }