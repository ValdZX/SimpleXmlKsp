plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(project(":core"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.10-1.0.0-beta02")
    implementation("com.squareup:kotlinpoet:1.8.0")
}