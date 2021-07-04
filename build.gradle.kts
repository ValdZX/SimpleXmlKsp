plugins {
    kotlin("jvm") version "1.5.21" apply false
    id("com.google.devtools.ksp") version "1.5.21-1.0.0-beta06" apply false
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.5.21"))
        classpath("com.android.tools.build:gradle:4.1.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}