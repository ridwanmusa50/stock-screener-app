import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.gradle.AppExtension

private val projectProperties = getProperties("values")
val signKeyAlias = projectProperties.getProperty("SIGN_KEY_ALIAS")!!
val signPw = projectProperties.getProperty("SIGN_PW")!!

plugins {
    kotlin("android")
    kotlin("plugin.serialization") version "2.1.21"
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "io.rid.stockscreenerapp"
    compileSdk = 35
    flavorDimensions += "version"

    defaultConfig {
        applicationId = "io.rid.stockscreenerapp"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("prod") {
            storeFile = file("keys/stock-screener-keystore.jks")
            keyAlias = signKeyAlias
            storePassword = signPw
            keyPassword = signPw
        }
    }

    buildTypes {

        getByName("release") {
            signingConfig = signingConfigs.getByName("prod")
        }
        fun AppExtension.configureBuildType(name: String) {
            val configureAction: ApplicationBuildType.() -> Unit = {
                isDebuggable = false
                isMinifyEnabled = false
                isShrinkResources = false
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

                buildConfigStringField("BASE_API_URL", "https://www.alphavantage.co")
                buildConfigFieldWithProperty("API_ACCESS_KEY", projectProperties)
            }

            if (names.contains(name)) {
                getByName(name, configureAction)
            } else {
                create(name, configureAction)
            }

            val debugName = "${name}Debug"
            create(debugName) {
                initWith(getByName(name))
                isDebuggable = true
                val debugAppName = "Stock Screener - Debug"
                resValue("string", "app_name", debugAppName)
            }
        }

        configureBuildType("release")
    }

    productFlavors {
        create("production") {
            dimension = "version"
        }
    }

    androidComponents {
        beforeVariants { variantBuilder ->
            // To specific certain build variant name
            if(
                variantBuilder.name != "productionReleaseDebug" &&
                variantBuilder.name != "productionRelease"
            ) {
                // Gradle ignores any variants
                variantBuilder.enable = false
            }
        }
    }

    applicationVariants.all {
        outputs.forEach { output ->
            if(output is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
                output.outputFileName = "${applicationId}-${name}-v${versionName}(${this.versionCode}).apk"
            }
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
        buildConfig = true
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