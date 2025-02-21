// Project-level build.gradle.kts
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // Ensure it's present
    }
    dependencies {
        // Safe Args Gradle Plugin
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.6")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
