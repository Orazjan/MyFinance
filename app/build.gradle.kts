plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    id("com.google.gms.google-services")
}
android {
    namespace = "com.example.myfinance"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myfinance"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "0.9.3"

        buildConfigField("boolean", "FIREBASE_DEBUG", "true")
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(21)
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

}

dependencies {
    // room
    val room_version = "2.8.4"
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:${room_version}")
    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.foundation)
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material.icons.extended)
    debugImplementation(libs.ui.tooling)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.coroutines.play.services)

    //  firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)



    // AndroidX & Material
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.transition)
    implementation(libs.material.tap.target.prompt)

    // Testing
    testImplementation(libs.junit)
}