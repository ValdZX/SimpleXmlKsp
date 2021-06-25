plugins {
    kotlin("jvm") version "1.5.20" apply false
    id("com.google.devtools.ksp") version "1.5.20-1.0.0-beta03" apply false
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.5.20"))
        classpath("com.android.tools.build:gradle:4.2.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}