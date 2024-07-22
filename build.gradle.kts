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
        classpath("com.android.tools.build:gradle:8.5.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}