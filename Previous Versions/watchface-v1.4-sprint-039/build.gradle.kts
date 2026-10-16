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
        versionCode = 8
        versionName = "1.4"

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
