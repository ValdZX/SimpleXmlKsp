import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
}

version = projectVersion

ksp {
    arg("simplexml.ksp.modulepackage", "ua.vald_zx.simplexml.ksp.test.custompackage")
    arg("simplexml.ksp.modulename", "Test")
}

kotlin {
    mingwX64()
    androidTarget()
    jvm()
    js {
        browser()
        nodejs()
    }
    watchos()
    macosX64()
    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iosTarget("ios") {}

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(project(":simplexml-ksp-core"))
                configurations["ksp"].dependencies.add(project.dependencies.create(project(":simplexml-ksp-processor")))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.simpleframework:simple-xml:2.7.1")
                configurations["ksp"].dependencies.add(project.dependencies.create(project(":simplexml-ksp-processor")))
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
    }
    sourceSets.all {
        kotlin.srcDir("build/generated/ksp/$name/kotlin")
    }
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        namespace = "ua.vald_zx.simplexml.ksp.test"
    }
}