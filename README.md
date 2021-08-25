[![Maven Central](https://img.shields.io/maven-central/v/io.github.valdzx/simplexml-ksp-core.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.valdzx%22)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

# SimpleXmlKsp

Kotlin Symbol Processing for XML serialization. Generates serializers by annotations

## Usage

Provide the following arguments to KSP in the module's `build.gradle.ksp`.

```kotlin
ksp {
    arg("simplexml.ksp.modulepackage", "com.my.app")
    arg("simplexml.ksp.modulename", "CurrentModuleName")
}
```

Enrol serializers on app startup:

```kotlin
CurrentModuleNameSerializersEnrolment.enrol()
```

Required XML:

```xml
<Package service="GET_INFO">
  <Token>S290bGluIGlzIGF3ZXNvbWU=</Token>
  <Location lat="50.004977" lng="36.231117">Ukraine, Kharkiv</Location>
</Package>
```

Use [annotations](#annotations) for construct xml structure:

```kotlin
@Root("Package")
data class PackageDto(
    @Attribute(name = "service")
    val serviceName: String,
    @Element(name = "Token")
    val token: String,
    @Element(name = "Location")
    val location: String,
    @Path("Location")
    @Attribute(name = "lat")
    val latitude: Double,
    @Path("Location")
    @Attribute(name = "lng")
    val longitude: Double,
)
```

Serialize:

```kotlin
val bean = PackageDto(
    serviceName = "GET_INFO",
    token = "S290bGluIGlzIGF3ZXNvbWU=",
    location = "Ukraine, Kharkiv",
    latitude = 50.004977,
    longitude = 36.231117
)
val xml: String = SimpleXml.serialize(bean)
```

Deserialize:

```kotlin
val deserializedBean: PackageDto = SimpleXml.deserialize(xml)
```

## Add to your project

1. Importing
   the [KSP plugin](https://github.com/google/ksp/blob/main/docs/quickstart.md#use-your-own-processor-in-a-project) in
   the project's `build.gradle.kts`

```gradle
plugins {
    id("com.google.devtools.ksp") version 1.5.21-1.0.0-beta07 apply false
}
```

##Annotations

###@Element

###@Root

###@Path

###@Attribute

###@ElementList

### Jvm / Android

```kotlin
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("io.github.valdzx:simplexml-ksp-core:1.0.0-dev04")
    ksp("io.github.valdzx:simplexml-ksp-processor:1.0.0-dev04")
}
```

Make sure that you have `mavenCentral()` in the list of repositories:

```kotlin
repository {
    mavenCentral()
}
```

### Multiplatform

Available for JVM, Android, iOS, JavaScript, macosX64

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}
val commonMain by getting {
    dependencies {
        implementation("io.github.valdzx:simplexml-ksp-core:1.0.0-dev04")
        configurations["ksp"].dependencies.add(project.dependencies.create("io.github.valdzx:simplexml-ksp-processor:1.0.0-dev04"))
    }
}
```

## Troubleshooting

<details><summary>Can't use the generated code on my IDE</summary>

You should set manually the source sets of the generated files, like
described [here](https://github.com/google/ksp/issues/37).

```gradle
buildTypes {
    debug {
        sourceSets {
            main.java.srcDirs += 'build/generated/ksp/debug/kotlin/'
        }
    }
    release {
        sourceSets {
            main.java.srcDirs += 'build/generated/ksp/release/kotlin/'
        }
    }
}
```

</details>

## License

```
Copyright 2021 Vladislav Khimichenko

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```