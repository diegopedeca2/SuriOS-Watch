plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val distributionProfile = providers.gradleProperty("distributionProfile")
    .orElse("MAIN")
    .get()
    .uppercase()
require(distributionProfile in setOf("MAIN", "FENRIR", "ALTAMIRA", "CHECHU")) {
    "Unknown distributionProfile: $distributionProfile"
}
val commonAssetsRoot = rootProject.file("assets")
val distributionResRoots = if (distributionProfile == "MAIN") {
    listOf(file("src/main/res"))
} else {
    listOf(file("src/main/res"), rootProject.file("distribution-res/$distributionProfile"))
}
val distributionAssetsRoots = if (distributionProfile == "MAIN") {
    listOf(file("src/main/assets"))
} else {
    listOf(
        rootProject.file("distribution-assets/common"),
        rootProject.file("distribution-assets/$distributionProfile")
    )
}

android {
    namespace = "com.suri.pipsurios"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.suri.pipsurios"
        minSdk = 34
        targetSdk = 37
        versionCode = 11
        versionName = "3.1"
        buildConfigField("String", "DISTRIBUTION_PROFILE", "\"$distributionProfile\"")
        buildConfigField("boolean", "PROBE_ENABLED", (distributionProfile == "MAIN").toString())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "edition"
    productFlavors {
        create("full") {
            dimension = "edition"
            buildConfigField("boolean", "PRS_ONLY", "false")
            if (distributionProfile != "MAIN") {
                applicationIdSuffix = ".${distributionProfile.lowercase()}"
            }
        }
        create("prsOnly") {
            dimension = "edition"
            applicationIdSuffix = ".prs"
            versionNameSuffix = "-prs"
            buildConfigField("boolean", "PRS_ONLY", "true")
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    androidResources {
        noCompress += listOf("mbtiles", "mp3")
    }
    sourceSets["main"].assets.directories.clear()
    sourceSets["main"].assets.directories.addAll(
        (distributionAssetsRoots + commonAssetsRoot).map { it.absolutePath }
    )
    sourceSets["main"].res.directories.clear()
    sourceSets["main"].res.directories.addAll(distributionResRoots.map { it.absolutePath })
    defaultConfig {
        manifestPlaceholders["appIcon"] = when (distributionProfile) {
            "FENRIR" -> "@drawable/pip_f_icon"
            "ALTAMIRA" -> "@drawable/pip_a_icon"
            "CHECHU" -> "@drawable/pip_c_icon"
            else -> "@mipmap/ic_launcher"
        }
        manifestPlaceholders["appLabel"] = when (distributionProfile) {
            "FENRIR" -> "PIP-SuriOS FENRIR"
            "ALTAMIRA" -> "PIP-SuriOS ALTAMIRA"
            "CHECHU" -> "PIP-SuriOS CHECHU"
            else -> "PIP-SuriOS MAIN"
        }
        manifestPlaceholders["probeEnabled"] = (distributionProfile == "MAIN").toString()
    }
}

dependencies {
    implementation(project(":probeprotocol"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.play.services.wearable)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
