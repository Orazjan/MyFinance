plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myfinance"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.example.myfinance"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "0.8"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation (libs.mpandroidchart)

    implementation (libs.facebook.android.sdk)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation (libs.facebook.login)
    implementation(libs.firebase.firestore)

    implementation(libs.room.runtime)
    implementation(libs.annotation)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.constraintlayout.core)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.paging)
    testImplementation(libs.room.testing)
    implementation(libs.room.ktx)

    implementation(libs.material)
    implementation (libs.viewpager2)
    implementation (libs.lifecycle.viewmodel)
    implementation (libs.lifecycle.livedata)
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}