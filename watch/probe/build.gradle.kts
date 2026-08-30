plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.suri.surioswatch.probe"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.suri.pipsurios"
        minSdk = 33
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":probeprotocol"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.play.services.location)
    implementation(libs.play.services.wearable)
}
