package ua.vald_zx.simplexml.ksp.test.map

import ua.vald_zx.simplexml.ksp.Attribute
import ua.vald_zx.simplexml.ksp.ElementMap
import ua.vald_zx.simplexml.ksp.Path
import ua.vald_zx.simplexml.ksp.Root

/*
<ConstructorFieldMapOfStrings>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</ConstructorFieldListOfStrings>
 */
data class ConstructorFieldMapOfStrings(
    @ElementMap(name = "Map", key = "Key", entry = "Value")
    val map: Map<String, Int>
)

/*
<ConstructorFieldMapOfStringsNullable>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</ConstructorFieldMapOfStringsNullable>
 */
data class ConstructorFieldMapOfStringsNullable(
    @ElementMap(name = "Map", key = "Key", entry = "Value")
    val map: Map<String, Int>?
)

/*
<ConstructorFieldMapOfStringsNullableDefaultNull>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</ConstructorFieldMapOfStringsNullableDefaultNull>
 */
data class ConstructorFieldMapOfStringsNullableDefaultNull(
    @ElementMap(name = "Map", key = "Key", entry = "Value")
    var map: Map<String, Int>? = null
)

/*
<FieldMapOfStringsWithDefault>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</FieldMapOfStringsWithDefault>
 */
class FieldMapOfStringsWithDefault {
    @ElementMap(name = "Map", key = "Key", entry = "Value")
    var map: Map<String, Int> = mutableMapOf()
}

/*
<FieldMapOfStringsNullable>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</FieldMapOfStringsNullable>
 */
class FieldMapOfStringsNullable {
    @ElementMap(name = "Map", key = "Key", entry = "Value")
    var map: Map<String, Int>? = null
}

/*
<FieldMapOfStringsNullable>
    <Map count="2">
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</FieldMapOfStringsNullable>
 */
class FieldMapOfStringsNullableWithAttribute {
    @ElementMap(name = "Map", key = "Key", entry = "Value")
    var map: Map<String, Int>? = null

    @Path("Map")
    @Attribute
    var count: Int = 0
}

/*
<FieldMapOfStringsNullable>
    <Map count="2">
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</FieldMapOfStringsNullable>
 */
class FieldMapOfStringsDefaultWithAttribute {

    @ElementMap(name = "Map", key = "Key", entry = "Value")
    var map: Map<String, Int> = mutableMapOf()

    @Path("Map")
    @Attribute
    var count: Int = 0
}

/*
<AndroidStringResourcesChild>
    <resources xmlns:android="http://schemas.android.com/apk/res/android">
        <string name="appName">The best app</string>
        <string name="greetings">Hello!</string>
    </resources>
</AndroidStringResourcesChild>
 */
data class AndroidStringResourcesChild(
    @ElementMap(name = "resources", key = "name", entry = "string", attribute = true)
    val resources: Map<String, String>,

    @field:[Path("resources") Attribute("xmlns:android")]
    var ns: String = "http://schemas.android.com/apk/res/android",
)

/*
<resources xmlns:android="http://schemas.android.com/apk/res/android">
    <string name="appName">The best app</string>
    <string name="greetings">Hello!</string>
</resources>
 */
@Root("resources")
data class AndroidStringResources(
    @ElementMap(inline = true, key = "name", entry = "string", attribute = true)
    val resources: Map<String, String>,

    @Attribute("xmlns:android")
    var ns: String = "http://schemas.android.com/apk/res/android",
)

/*
<FieldAndroidStringResourcesChild>
    <resources xmlns:android="http://schemas.android.com/apk/res/android">
        <string name="appName">The best app</string>
        <string name="greetings">Hello!</string>
    </resources>
</FieldAndroidStringResourcesChild>
 */
class FieldAndroidStringResourcesChild {
    @ElementMap(name = "resources", key = "name", entry = "string", attribute = true)
    var resources: Map<String, String> = emptyMap()

    @field:[Path("resources") Attribute("xmlns:android")]
    var ns: String = "http://schemas.android.com/apk/res/android"
}

/*
<resources xmlns:android="http://schemas.android.com/apk/res/android">
    <string name="appName">The best app</string>
    <string name="greetings">Hello!</string>
</resources>
 */
@Root("resources")
class FieldAndroidStringResources {
    @ElementMap(inline = true, key = "name", entry = "string", attribute = true)
    var resources: Map<String, String> = emptyMap()

    @Attribute("xmlns:android")
    var ns: String = "http://schemas.android.com/apk/res/android"
}

/*
<resources xmlns:android="http://schemas.android.com/apk/res/android">
    <string name="appName">The best app</string>
    <string name="greetings">Hello!</string>
</resources>
 */
@Root("resources")
class NullableFieldAndroidStringResources {
    @ElementMap(inline = true, key = "name", entry = "string", attribute = true)
    var resources: Map<String, String>? = null

    @Attribute("xmlns:android")
    var ns: String = "http://schemas.android.com/apk/res/android"
}