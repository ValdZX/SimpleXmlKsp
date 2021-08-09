plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(project(":simplexml-ksp-core"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.21-1.0.0-beta06")
    implementation("com.squareup:kotlinpoet:1.8.0")
    kspTest(project(":simplexml-ksp-processor"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-common"))
    testImplementation(kotlin("test-annotations-common"))
}