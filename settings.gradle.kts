rootProject.name = "SimpleXmlKsp"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

include("core")
include("processor")
include("test:kmm")
