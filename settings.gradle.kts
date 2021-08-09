rootProject.name = "SimpleXmlKsp"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

include("simplexml-ksp-core")
include("simplexml-ksp-processor")
include("simplexml-ksp-test:kmm")
