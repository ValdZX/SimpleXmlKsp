package ua.vald_zx.simplexml.ksp.test.map

import ua.vald_zx.simplexml.ksp.Attribute
import ua.vald_zx.simplexml.ksp.ElementMap
import ua.vald_zx.simplexml.ksp.Path

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
<ConstructorGenericMapOfStrings>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</ConstructorGenericMapOfStrings>
 */
data class ConstructorGenericMapOfStrings<K, V>(
    @ElementMap(name = "Map", key = "Key", entry = "Value")
    val map: Map<K, V>
)

/*
<ConstructorKeyGenericMapOfStrings>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</ConstructorKeyGenericMapOfStrings>
 */
data class ConstructorKeyGenericMapOfStrings<K>(
    @ElementMap(name = "Map", key = "Key", entry = "Value")
    val map: Map<K, Int>
)

/*
<ConstructorValueGenericMapOfStrings>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</ConstructorValueGenericMapOfStrings>
 */
data class ConstructorValueGenericMapOfStrings<V>(
    @ElementMap(name = "Map", key = "Key", entry = "Value")
    val map: Map<String, V>
)

/*
<ConstructorGenericInlineMapOfStrings>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</ConstructorGenericInlineMapOfStrings>
 */
data class ConstructorGenericInlineMapOfStrings<K, V>(
    @ElementMap(inline = true, key = "Key", entry = "Value")
    val map: Map<K, V>
)

/*
<ConstructorKeyGenericInlineMapOfStrings>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</ConstructorKeyGenericInlineMapOfStrings>
 */
data class ConstructorKeyGenericInlineMapOfStrings<K>(
    @ElementMap(inline = true, key = "Key", entry = "Value")
    val map: Map<K, Int>
)

/*
<ConstructorValueGenericInlineMapOfStrings>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</ConstructorValueGenericInlineMapOfStrings>
 */
data class ConstructorValueGenericInlineMapOfStrings<V>(
    @ElementMap(inline = true, key = "Key", entry = "Value")
    val map: Map<String, V>
)

/*
<FieldGenericMapOfStringsWithDefault>
    <Map>
        <Key>ErrorCode</Key>
        <Value>404</Value>
        <Key>DataSize</Key>
        <Value>762</Value>
    </Map>
</FieldGenericMapOfStringsWithDefault>
 */
class FieldGenericMapOfStringsWithDefault<K, V> {
    @ElementMap(name = "Map", key = "Key", entry = "Value")
    var map: Map<K, V> = mutableMapOf()
}