plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.suri.surioswatch.probewatchface"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.suri.surioswatch.probewatchface"
        minSdk = 33
        targetSdk = 37
        versionCode = 3
        versionName = "2.2"
    }

    // Watch Face Format is resource-only; Wear OS renders the XML document.
    enableKotlin = false
}

dependencies {
}
