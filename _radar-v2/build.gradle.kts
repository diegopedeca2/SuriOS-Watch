plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.suri.suriosradar"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.suri.suriosradar.v2"
        minSdk = 34
        targetSdk = 37
        versionCode = 2
        versionName = "1.1-radar-v2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets {
        getByName("main") {
            val sharedKotlin = "../_retired-radar-20261025/watch-radarwatch/src/main/java"
            java.srcDirs(sharedKotlin, "src/main/java")
            kotlin.srcDirs(
                sharedKotlin,
                "src/main/java"
            )
            res.srcDirs("../_retired-radar-20261025/watch-radarwatch/src/main/res")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
        getByName("test") {
            java.srcDirs("src/test/java")
            kotlin.srcDirs("src/test/java")
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.wear.compose.material3)

    testImplementation(libs.junit)
}
