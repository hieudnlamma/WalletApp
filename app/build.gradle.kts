import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.dev.ksp)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
    // alias(libs.plugins.google.services)
    // alias(libs.plugins.crashlytics)
}

android {
    namespace = "com.lmt.global.base"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.lmt.global.base"
        minSdk = 24
        targetSdk = 36
        versionCode = 100
        versionName = "1.0.0-dev"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val formatter = DateTimeFormatter.ofPattern("MM.dd.yyyy")
        val formattedDate = LocalDate.now().format(formatter)
        setProperty(
            "archivesBaseName",
            "AppName_v${versionName}_v${versionCode}_${formattedDate}"
        )

        buildConfigField(
            "String",
            "WALLET_INTER_SPLASH",
            "\"ca-app-pub-3940256099942544/1033173712\""
        )
    }

    buildTypes {
        debug {
            manifestPlaceholders["ad_app_id"] = "ca-app-pub-3940256099942544~3347511713"

            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }

        release {
            manifestPlaceholders["ad_app_id"] = "ca-app-pub-3940256099942544~3347511713"

            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {

    implementation(project(":ads"))
    implementation(project(":lamma-consent"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.glide)
    implementation(libs.glide.transform)
    implementation(libs.rounded.image.view)
    implementation(libs.dots.indicator)
    implementation(libs.lottie)
    implementation(libs.blurview)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Room Database
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    // Network & Retrofit
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.gson)
    // Others
    implementation(libs.permissions.dispatcher)
    implementation(libs.threetenabp)
    implementation(libs.androidx.paging)
    implementation("com.sun.mail:android-mail:1.6.2")
    implementation("com.sun.mail:android-activation:1.6.2")

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
}
