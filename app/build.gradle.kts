import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms.google.services)
}

val localPropertiesFile: File = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.ralphmarondev.velora"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ralphmarondev.velora"
        minSdk = 28
        targetSdk = 36
        versionCode = 2
        versionName = "1.0"

        manifestPlaceholders["MAPS_API_KEY"] = localProperties["MAPS_API_KEY"]?.toString() ?: ""
        buildConfigField(
            type = "String",
            name = "MAPS_API_KEY",
            value = "\"${localProperties["MAPS_API_KEY"]?.toString() ?: ""}\""
        )
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties["RELEASE_STORE_FILE"] ?: "release.keystore")
            storePassword = localProperties["RELEASE_STORE_PASSWORD"]?.toString()
            keyAlias = localProperties["RELEASE_KEY_ALIAS"]?.toString()
            keyPassword = localProperties["RELEASE_KEY_PASSWORD"]?.toString()
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material.icons.extended.android)
    implementation(libs.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.maps)
    debugImplementation(libs.androidx.compose.ui.tooling)
}