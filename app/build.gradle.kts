plugins {
    alias(libs.plugins.android.application)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.kavindu.farmshare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kavindu.farmshare"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "URL", "\"http://192.168.8.155:8080/api\"")
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.daimajia.androidanimations:library:2.4@aar")
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("com.github.timqi:SectorProgressView:f7243c322f")
    implementation("me.zhanghai.android.materialprogressbar:library:1.6.1")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation ("com.github.bumptech.glide:glide:4.15.1")

    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.google.code.gson:gson:2.11.0")
    implementation ("com.github.taimoorsultani:android-sweetalert2:2.0.2")
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-storage")



}