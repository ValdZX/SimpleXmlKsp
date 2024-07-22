plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("signing")
}

group = projectGroup
version = projectVersion

repositories {
    mavenCentral()
}

kotlin {
    mingwX64()
    androidTarget() {
        publishLibraryVariants("release", "debug")
    }
    jvm()
    ios()
    js {
        browser()
        nodejs()
    }
    watchos()
    macosX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(kotlin("reflect"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}

android {
    compileSdk = 34

    defaultConfig {
        namespace = "ua.vald_zx.simplexml.ksp"
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res", "src/commonMain/resources")
        }
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

val sonatypeUsername: String? = project.properties["sonatype.login"]?.toString()
val sonatypePassword: String? = project.properties["sonatype.password"]?.toString()
publishing {
    publications {
        repositories {
            maven {
                name = "snapshot"
                url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
            maven {
                name = "staging"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
        }
        withType<MavenPublication> {
            artifact(javadocJar.get())
            pom {
                name.set("SimpleXmlKsp")
                description.set("Simple XML Ksp - Core")
                url.set("https://github.com/ValdZX/SimpleXmlKsp")
                licenses {
                    license {
                        name.set("Apache License")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/ValdZX/SimpleXmlKsp/issues")
                }
                scm {
                    connection.set("https://github.com/ValdZX/SimpleXmlKsp.git")
                    url.set("https://github.com/ValdZX/SimpleXmlKsp")
                }
                developers {
                    developer {
                        id.set("Vald_ZX")
                        name.set("Vladislav Khimichenko")
                        email.set("khimichenko.vladislav@gmail.com")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications)
}