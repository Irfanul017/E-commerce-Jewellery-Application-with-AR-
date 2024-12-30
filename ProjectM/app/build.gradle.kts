import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.majorproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.majorproject"
        minSdk = 28
        targetSdk = 34
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx.v1101)
    implementation(libs.kotlin.stdlib)
    implementation(libs.picasso)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.firebase.storage)
    implementation(libs.play.services.maps)
    implementation(libs.lottie)
    implementation(platform(libs.kotlin.bom))
    implementation(libs.smoothbottombar)
    implementation(libs.motiontoast)
    implementation(libs.library)
    implementation(libs.dotsindicator)
    implementation(libs.blurry)
    implementation(libs.glide)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.core.animation)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.devrel.easypermissions)
    implementation(libs.devrel.easypermissions)
    implementation(libs.play.services.wallet)
    implementation (libs.play.services.ads.identifier)
    implementation ("com.onesignal:OneSignal:[5.0.0, 5.99.99]")




}
