plugins {
    kotlin("jvm") version "1.5.10" apply false
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.5.10"))
        classpath("com.android.tools.build:gradle:4.2.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}