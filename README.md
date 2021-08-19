[![Maven Central](https://img.shields.io/maven-central/v/io.github.valdzx/simplexml-ksp-core.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.valdzx%22)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
# SimpleXmlKsp
Kotlin Symbol Processing for XML serialization. Generates serializers by annotations
## Setup
### Gradle

Add [Google KSP](https://github.com/google/ksp/blob/main/docs/quickstart.md) and dependencies (you can also add other modules that you need):

```kotlin
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("io.github.valdzx:simplexml-ksp-core:1.0.0-dev03")
    ksp("io.github.valdzx:simplexml-ksp-processor:1.0.0-dev03")
}
```

Make sure that you have `mavenCentral()` in the list of repositories:

```kotlin
repository {
    mavenCentral()
}
```

### Multiplatform

Available for JVM, Android, iOS

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}
val commonMain by getting {
    dependencies {
        implementation("io.github.valdzx:simplexml-ksp-core:1.0.0-dev03")
        configurations["ksp"].dependencies.add(project.dependencies.create("io.github.valdzx:simplexml-ksp-processor:1.0.0-dev03"))
    }
}
```


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