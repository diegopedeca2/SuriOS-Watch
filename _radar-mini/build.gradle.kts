plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    // Las pantallas compartidas del proyecto usan el R del paquete original.
    // El nombre instalado sigue siendo independiente mediante applicationId.
    namespace = "com.suri.suriosradar"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.suri.suriosradar.mini"
        minSdk = 34
        targetSdk = 37
        versionCode = 1
        versionName = "1.0-mini-radar"
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
            kotlin.srcDirs(sharedKotlin, "src/main/java")
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
