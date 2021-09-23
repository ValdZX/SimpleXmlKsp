plugins {
    kotlin("jvm") version kotlinVersion apply false
    id("com.google.devtools.ksp") version kspVersion apply false
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.android.tools.build:gradle:7.0.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}