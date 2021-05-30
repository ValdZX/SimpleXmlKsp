plugins {
    kotlin("jvm") version "1.5.10" apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.5.10"))
        classpath("com.android.tools.build:gradle:4.0.1")
    }
}