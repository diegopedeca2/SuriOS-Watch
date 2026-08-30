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
        minSdk = 33
        targetSdk = 37
        versionCode = 5
        versionName = "1.1"

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
