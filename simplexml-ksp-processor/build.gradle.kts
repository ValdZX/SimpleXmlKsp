plugins {
    kotlin("jvm")
    `java-library`
    id("com.google.devtools.ksp")
    id("maven-publish")
    id("signing")
}

repositories {
    mavenCentral()
    google()
}

java {
    withSourcesJar()
}

dependencies {
    implementation(project(":simplexml-ksp-core"))
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("com.squareup:kotlinpoet:1.8.0")
    kspTest(project(":simplexml-ksp-processor"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-common"))
    testImplementation(kotlin("test-annotations-common"))
}

val sonatypeUsername: String? = project.properties["sonatype.login"]?.toString()
val sonatypePassword: String? = project.properties["sonatype.password"]?.toString()
publishing {
    publications {
        repositories {
            create<MavenPublication>("jvm") {
                groupId = projectGroup
                artifactId = "simplexml-ksp-processor"
                version = projectVersion

                from(components["java"])
            }
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
            pom {
                description.set("Simple XML Ksp - Processor")
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
    sign(publishing.publications)
}