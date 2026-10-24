import java.io.FileInputStream
import java.util.Properties

val signingPropertiesFile = rootProject.file("watch/watchface/signing.properties")
val signingProperties = Properties()
if (signingPropertiesFile.exists()) {
    FileInputStream(signingPropertiesFile).use { signingProperties.load(it) }
}

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
        versionCode = 12
        versionName = "1.8"

    }

    signingConfigs {
        create("release") {
            storeFile = file(signingProperties.getProperty("storeFile"))
            storePassword = signingProperties.getProperty("storePassword")
            keyAlias = signingProperties.getProperty("keyAlias")
            keyPassword = signingProperties.getProperty("keyPassword")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    enableKotlin = false
}

dependencies {
}
