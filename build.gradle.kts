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
        classpath("com.android.tools.build:gradle:4.1.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}