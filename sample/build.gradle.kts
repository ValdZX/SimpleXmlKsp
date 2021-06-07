plugins {
    id("com.google.devtools.ksp") version "1.5.10-1.0.0-beta01"
    kotlin("jvm")
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

sourceSets {
    getByName("main") {
        java.srcDir(File("$buildDir/generated/ksp/main/kotlin"))
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":core"))
    ksp(project(":processor"))
}
