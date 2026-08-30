pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PIP-SuriOS"
include(":app")
include(":watchface")
project(":watchface").projectDir = file("watch/watchface")
include(":watchfacev2")
project(":watchfacev2").projectDir = file("watch/watchfacev2")
include(":probeprotocol")
project(":probeprotocol").projectDir = file("watch/probeprotocol")
include(":probe")
project(":probe").projectDir = file("watch/probe")
