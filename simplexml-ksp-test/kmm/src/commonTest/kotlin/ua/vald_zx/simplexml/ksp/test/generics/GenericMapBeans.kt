package ua.vald_zx.simplexml.ksp.test.generics

import ua.vald_zx.simplexml.ksp.ElementMap

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