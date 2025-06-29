plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler) // Added Compose Compiler plugin for Kotlin 2.0+
    alias(libs.plugins.ksp) // KSP instead of KAPT for Room annotation processing
}

android {
    namespace = "com.example.project_prm392"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.project_prm392"
        minSdk = 25
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
        viewBinding = false // Disabled by default with Compose
        compose = true // Enable Compose features
    }

    // No longer needed with the Compose Compiler plugin
    // The plugin handles compiler configuration automatically
}

dependencies {
    // Core Android libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler) // Changed from kapt to ksp

    // Google Maps
    implementation(libs.google.maps)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)

    // Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.compose) // Added for Compose navigation

    // Coroutines
    implementation(libs.coroutines.android)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling) // For Compose tooling in debug mode
    debugImplementation(libs.androidx.compose.ui.test.manifest) // For UI tests
    androidTestImplementation(libs.androidx.compose.ui.test.junit4) // For Compose UI tests

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.coil.compose)
    // Material Icons Extended (for FilterList, Sort, Image, AddShoppingCart icons)
    implementation(libs.androidx.compose.material.icons.extended)
}