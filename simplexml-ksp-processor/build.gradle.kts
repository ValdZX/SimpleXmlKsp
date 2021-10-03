plugins {
    kotlin("jvm")
    `java-library`
    id("com.google.devtools.ksp")
    id("maven-publish")
    id("signing")
}

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
//    implementation("io.github.valdzx:simplexml-ksp-core-jvm:$projectVersion")
    implementation(project(":simplexml-ksp-core"))
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("com.squareup:kotlinpoet:1.10.1")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-common"))
    testImplementation(kotlin("test-annotations-common"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.4")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.4")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
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
            artifact(javadocJar.get())
            pom {
                name.set("SimpleXmlKsp")
                description.set("Simple XML Ksp - Processor")
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