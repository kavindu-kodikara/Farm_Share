plugins {
    alias(libs.plugins.android.application)
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


}