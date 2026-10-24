import java.io.FileInputStream
import java.util.Properties

val signingPropertiesFile = rootProject.file("watch/watchfaceely/signing.properties")
val signingProperties = Properties()
if (signingPropertiesFile.exists()) {
    FileInputStream(signingPropertiesFile).use { signingProperties.load(it) }
}

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.suri.surioswatch.ely"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.suri.surioswatch.ely"
        minSdk = 34
        targetSdk = 37
        versionCode = 5
        versionName = "2.0"
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

    // Watch Face Format is resource-only; Wear OS renders the XML document.
    enableKotlin = false
}

dependencies {
}
