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
    alias(libs.plugins.kotlin.kapt)
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

        javaCompileOptions {

            annotationProcessorOptions {

                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/src/main/assets/schemas",
                    "room.incremental" to "true"
                )

            }

        }
    }

    signingConfigs {
        create("prod") {
            storeFile = file("keys/stock-screener-keystore.jks")
            keyAlias = signKeyAlias
            storePassword = signPw
            keyPassword = signPw
        }

        getByName("debug") {
            storeFile = file("keys/stock-screener-keystore.jks")
            keyAlias = signKeyAlias
            storePassword = signPw
            keyPassword = signPw
        }
    }

    buildTypes {

        fun AppExtension.configureBuildType(buildType: String, isProduction: Boolean = false) {
            val configureAction: ApplicationBuildType.() -> Unit = {
                isDebuggable = false
                isMinifyEnabled = false
                isShrinkResources = false
                signingConfig = signingConfigs.getByName("prod")

                if (!isProduction) matchingFallbacks += listOf("debug")
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

                buildConfigFieldWithProperty("API_ACCESS_KEY", projectProperties)
            }

            if (names.contains(buildType)) {
                getByName(buildType, configureAction)
            } else {
                val baseBuildType = if (buildType != "release" && buildType.contains("release")) getByName("release") else null

                create(buildType) {
                    baseBuildType?.let { initWith(it) }
                    configureAction(this)
                }
            }

            // Debuggable build
            create("${buildType}Debug") {
                initWith(getByName(buildType))
                isDebuggable = true
                signingConfig = signingConfigs.getByName("prod")

                resValue("string", "app_name", "Stock Screener - Debug")
            }
        }

        configureBuildType("development")
        configureBuildType("release")
    }

    productFlavors {

        // Development environment
        create("dev") {
            applicationIdSuffix = ".dev" // io.rid.stockscreenapp.dev
            dimension = "version"
        }

        create("production") {
            dimension = "version"
        }
    }

    androidComponents {
        beforeVariants { variantBuilder ->
            // To specific certain build variant name
            if(
                variantBuilder.name != "devDevelopment" &&
                variantBuilder.name != "devDevelopmentDebug" &&
                variantBuilder.name != "productionRelease" &&
                variantBuilder.name != "productionReleaseDebug"
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

    kotlin.sourceSets.all {
        languageSettings.optIn("androidx.compose.foundation.layout.ExperimentalLayoutApi")
        languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
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
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.constraintlayout.compose)

    // =============================================================================================================
    // Google
    // =============================================================================================================
    implementation(libs.google.gson)

    // =============================================================================================================
    // Retrofit
    // =============================================================================================================
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter.gson)
    implementation(libs.squareup.retrofit.converter.scalars)
    implementation(libs.squareup.retrofit.adapter.rxjava2)

    // =============================================================================================================
    // Okhttp
    // =============================================================================================================
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.okhttp.logging.interceptor)

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
    kapt(libs.google.dagger.hilt.compiler)

    // =============================================================================================================
    // Room Database
    // =============================================================================================================
    implementation(libs.androidx.room.room.ktx)
    implementation(libs.androidx.room.room.runtime)
    ksp(libs.androidx.room.room.compiler)

    // =============================================================================================================
    // Datastore
    // =============================================================================================================
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)

    // =============================================================================================================
    // Testing
    // =============================================================================================================
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // =============================================================================================================
    // Other Libraries
    // =============================================================================================================

    // Coil - Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // Jsoizo - Kotlin CSV
    implementation(libs.opencsv.opencsv)
}

// Allow references to generated code
kapt { correctErrorTypes = true }
hilt { enableAggregatingTask = false }