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
    android()
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
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(project(":simplexml-ksp-core"))
                configurations["ksp"].dependencies.add(project.dependencies.create(project(":simplexml-ksp-processor")))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.simpleframework:simple-xml:2.7.1")
            }
        }
        val androidMain by getting
        val androidAndroidTestRelease by getting
        val androidTest by getting {
            dependsOn(androidAndroidTestRelease)
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting
        val iosTest by getting
    }
    sourceSets.all {
        kotlin.srcDir("build/generated/ksp/$name/kotlin")
    }
}

android {
    compileSdkVersion(31)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(31)
    }
}