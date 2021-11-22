[![Maven Central](https://img.shields.io/maven-central/v/io.github.valdzx/simplexml-ksp-core.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.valdzx%22)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

# SimpleXmlKsp

Generator of bean serializators for XML serialization. Based on [Google KSP](https://github.com/google/ksp)

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

####Serialize:

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

####Deserialize:

```kotlin
val deserializedBean: PackageDto = SimpleXml.deserialize(xml)
```

## Annotations

### @Element
The Element annotation is used to represent a field or method that appears as an XML element. Fields or methods that are annotated with this can be either primitive or compound, that is, represent an object that can be serialized and deserialized. Below is an example of the serialized format for a compound object.
### @Text
The Text annotation is used to represent a field or method that appears as text within an XML element.
### @Root
This Root annotation is used to annotate classes that need to be serialized.
### @Path
The Path annotation is used to specify an XML path where an XML element or attribute is located.
### @Attribute
The Attribute annotation represents a serializable XML attribute within an XML element. An object annotated with this is typically a primitive or enumerated type. Conversion from the attribute to primitive type is done with a Transform object. If a suitable transform can be found then this will convert the attribute string value to an object instance, which can be assigned to the annotated field, or passed to the annotated method.
### @ElementList
The ElementList annotation represents a method or field that is a List for storing entries. However, a class attribute can be used to override the field type, however the type must be assignable.
```xml
<resources xmlns:android="http://schemas.android.com/apk/res/android">
    <string name="appName">The best app</string>
    <string name="greetings">Hello!</string>
</resources>
```

```kotlin
@Root("string")
data class StringResource(
    @Attribute(name = "name")
    val name: String,
    @Text
    val value: String
)

@Root("resources")
data class StringResources(
    @ElementList(inline = true, entry = "string")
    val strings: List<StringResource>,
    @Attribute(name = "xmlns:android")
    var androidNs: String = "http://schemas.android.com/apk/res/android"
)
```

### @ElementMap
The ElementMap annotation represents a method or field that is a Map for storing key value pairs.
```xml
<resources xmlns:android="http://schemas.android.com/apk/res/android">
    <string name="appName">The best app</string>
    <string name="greetings">Hello!</string>
</resources>
```

```kotlin
@Root("resources")
data class StringResourcesMap(
   @ElementMap(inline = true, entry = "string", key = "name", attribute = true)
   val strings: Map<String, String>,
   @Attribute(name = "xmlns:android")
   var androidNs: String = "http://schemas.android.com/apk/res/android"
)
```


## Add to your project

1. Importing
   the [KSP plugin](https://github.com/google/ksp/blob/main/docs/quickstart.md#use-your-own-processor-in-a-project) in
   the project's `build.gradle.kts`

```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.6.0-1.0.1" apply false
}
```

### Jvm / Android

```kotlin
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("io.github.valdzx:simplexml-ksp-core:1.0.0-alpha01")
    ksp("io.github.valdzx:simplexml-ksp-processor:1.0.0-alpha01")
}
```

Make sure that you have `mavenCentral()` in the list of repositories:

```kotlin
repository {
    mavenCentral()
}
```

###Kotlin multiplatform multiverse

Available for JVM, Android, iOS, JavaScript, macosX64

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}
val commonMain by getting {
    dependencies {
        implementation("io.github.valdzx:simplexml-ksp-core:1.0.0-alpha01")
        configurations["ksp"].dependencies.add(project.dependencies.create("io.github.valdzx:simplexml-ksp-processor:1.0.0-alpha01"))
    }
}
```

## Troubleshooting

<details><summary>Can't use the generated code on my IDE</summary>

You should set manually the source sets of the generated files, like
described [here](https://github.com/google/ksp/issues/37).

###Kotlin multiplatform multiverse:
```kotlin
kotlin {
   ...
   sourceSets.all {
      kotlin.srcDir("build/generated/ksp/$name/kotlin")
   }
}
```

###Android:
```kotlin
applicationVariants.all {
   val variantName = name
   sourceSets {
      getByName("main") {
          java.srcDir(File("build/generated/ksp/$variantName/kotlin"))
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