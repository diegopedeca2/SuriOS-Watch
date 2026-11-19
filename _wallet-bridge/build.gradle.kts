plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.suri.surioswallet"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.suri.surioswallet"
        minSdk = 34
        targetSdk = 37
        versionCode = 1
        versionName = "1.0-bridge"
    }
}
