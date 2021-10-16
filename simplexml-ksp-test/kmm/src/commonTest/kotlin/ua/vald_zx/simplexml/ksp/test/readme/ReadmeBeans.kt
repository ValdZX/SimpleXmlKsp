package ua.vald_zx.simplexml.ksp.test.readme

import ua.vald_zx.simplexml.ksp.*

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

/*
<resources xmlns:android="http://schemas.android.com/apk/res/android">
    <string name="appName">The best app</string>
    <string name="greetings">Hello!</string>
</resources>
 */

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

/*
<resources xmlns:android="http://schemas.android.com/apk/res/android">
    <string name="appName">The best app</string>
    <string name="greetings">Hello!</string>
</resources>
 */


@Root("resources")
data class StringResourcesMap(
    @ElementMap(inline = true, entry = "string", key = "name", attribute = true)
    val strings: Map<String, String>,
    @Attribute(name = "xmlns:android")
    var androidNs: String = "http://schemas.android.com/apk/res/android"
)
