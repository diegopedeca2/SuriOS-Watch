plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.suri.surioswatch"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.suri.surioswatch"
        minSdk = 34
        targetSdk = 37
        versionCode = 11
        versionName = "1.7"

    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    enableKotlin = false
}

dependencies {
}
