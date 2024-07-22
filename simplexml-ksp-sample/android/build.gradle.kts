plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp")
}

ksp {
    arg("simplexml.ksp.modulepackage", "ua.vald_zx.simplexml.ksp.sample.serializaton")
    arg("simplexml.ksp.modulename", "App")
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        version = "1.0"
        namespace = "ua.vald_zx.simplexml.ksp.sample"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    applicationVariants.all {
        val variantName = name
        sourceSets {
            getByName("main") {
                java.srcDir(File("build/generated/ksp/$variantName/kotlin"))
            }
        }
    }
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("io.github.valdzx:simplexml-ksp-core-jvm:1.0.0-dev11")
    ksp("io.github.valdzx:simplexml-ksp-processor:1.0.0-dev11")
}