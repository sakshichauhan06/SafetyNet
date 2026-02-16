import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.secrets.gradle.plugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.safetynet"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.safetynet"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
    sourceSets {
        getByName("main").assets.srcDirs(File("$projectDir/schemas"))
    }
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion("2.1.0")
            }
            if (requested.group == "org.jetbrains.kotlinx" && requested.name.contains("serialization")) {
                useVersion("1.8.1")
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")

    // Android Maps Compose composables for the Maps SDK for Android
    implementation("com.google.maps.android:maps-compose:6.11.0")

    // google map services
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // google maps utils
    implementation("com.google.maps.android:android-maps-utils:3.19.0")

    implementation("com.jakewharton.timber:timber:5.0.1")

    // Room DB setup
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.kotlinx.serialization.json)

    // For Icons.Filled, Icons.Outlined, etc.
    implementation("androidx.compose.material:material-icons-extended")

    // NavHost
    // implementation(libs.androidx.navigation.compose)
    implementation("androidx.navigation:navigation-compose:2.9.6")

    // Coil
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("io.coil-kt:coil-svg:2.2.2")

    // splash screen
    implementation("androidx.core:core-splashscreen:1.2.0")

    //Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))

    // Firebase authentication
    implementation("com.google.firebase:firebase-auth")

    // Firebase Cloud Firestore
    implementation("com.google.firebase:firebase-firestore")

    implementation("androidx.compose.runtime:runtime-livedata:1.6.0")

    // Dagger-Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
//    implementation("com.google.dagger:hilt-android:2.59")
//    ksp("com.google.dagger:hilt-android-compiler:2.59")
//    ksp("androidx.hilt:hilt-compiler:1.3.0")
//    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation("androidx.customview:customview-poolingcontainer:1.1.0")

}