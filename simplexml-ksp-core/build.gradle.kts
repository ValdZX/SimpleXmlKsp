plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("signing")
}

group = "io.github.valdzx"
version = "1.0.0-alpha01"

repositories {
    mavenCentral()
}

kotlin {
    android()
    jvm()
    ios()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidAndroidTestRelease by getting
        val androidTest by getting {
            dependsOn(androidAndroidTestRelease)
        }
    }
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
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

val sonatypeUsername: String? = System.getenv("SONATYPE_USERNAME")
val sonatypePassword: String? = System.getenv("SONATYPE_PASSWORD")
val repositoryId: String? = System.getenv("SONATYPE_REPOSITORY_ID")
publishing {
    publications {
        repositories {
            maven {
                name = "oss"
                url = uri("https://oss.sonatype.org/service/local/staging/deployByRepositoryId/$repositoryId/")
                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
        }
        withType<MavenPublication> {
            pom {
                name.set("SimpleXmlKsp")
                description.set("KODEIN Dependency Injection Core")
                licenses {
                    license {
                        name.set("Apache License")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                url.set("https://github.com/ValdZX/SimpleXmlKsp")
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
                        name.set("Vladislav Khimichenko")
                        email.set("khimichenko.vladislav@gmail.com")
                    }
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        System.getenv("GPG_PRIVATE_KEY"),
        System.getenv("GPG_PRIVATE_PASSWORD")
    )
    sign(publishing.publications)
}