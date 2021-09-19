package ua.vald_zx.simplexml.ksp.test.map

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.TestSerializersEnrolment
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MapTests {

    @BeforeTest
    fun init() {
        TestSerializersEnrolment.enrol()
    }

    @Test
    @JsName("ConstructorFieldMapOfStringsTest")
    fun `ConstructorFieldMapOfStrings serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = ConstructorFieldMapOfStrings(
            mapOf(
                "DataSize" to 2048,
                "ErrorCode" to 201
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldMapOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorFieldMapOfStringsNullableTest")
    fun `ConstructorFieldMapOfStringsNullable serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = ConstructorFieldMapOfStringsNullable(
            mapOf(
                "DataSize" to 2048,
                "ErrorCode" to 201
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldMapOfStringsNullable = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorFieldMapOfStringsNullableDefaultNullTest")
    fun `ConstructorFieldMapOfStringsNullableDefaultNull serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = ConstructorFieldMapOfStringsNullableDefaultNull(
            mapOf(
                "DataSize" to 2048,
                "ErrorCode" to 201
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldMapOfStringsNullableDefaultNull = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("FieldMapOfStringsWithDefaultTest")
    fun `FieldMapOfStringsWithDefault serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = FieldMapOfStringsWithDefault()
        bean.map = mapOf(
            "DataSize" to 2048,
            "ErrorCode" to 201
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldMapOfStringsWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean.map, deserializedBean.map)
    }

    @Test
    @JsName("FieldMapOfStringsNullableTest")
    fun `FieldMapOfStringsNullable serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = FieldMapOfStringsNullable()
        bean.map = mapOf(
            "DataSize" to 2048,
            "ErrorCode" to 201
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldMapOfStringsNullable = SimpleXml.deserialize(xml)
        assertEquals(bean.map, deserializedBean.map)
    }

    @Test
    @JsName("FieldMapOfStringsNullableWithAttributeTest")
    fun `FieldMapOfStringsNullableWithAttribute serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = FieldMapOfStringsNullableWithAttribute()
        bean.map = mapOf(
            "DataSize" to 2048,
            "ErrorCode" to 201
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldMapOfStringsNullableWithAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean.map, deserializedBean.map)
    }

    @Test
    @JsName("FieldMapOfStringsDefaultWithAttributeTest")
    fun `FieldMapOfStringsDefaultWithAttribute serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = FieldMapOfStringsDefaultWithAttribute()
        bean.map = mapOf(
            "DataSize" to 2048,
            "ErrorCode" to 201
        )
        bean.count = 2
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldMapOfStringsDefaultWithAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean.map, deserializedBean.map)
        assertEquals(bean.count, deserializedBean.count)
    }

    @Test
    @JsName("ConstructorGenericMapOfStringsTest")
    fun `ConstructorGenericMapOfStrings serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = ConstructorGenericMapOfStrings(
            mapOf(
                "DataSize" to 2048,
                "ErrorCode" to 201
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorGenericMapOfStrings<String, Int> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorKeyGenericMapOfStringsTest")
    fun `ConstructorKeyGenericMapOfStrings serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = ConstructorKeyGenericMapOfStrings(
            mapOf(
                "DataSize" to 2048,
                "ErrorCode" to 201
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorKeyGenericMapOfStrings<String> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorValueGenericMapOfStringsTest")
    fun `ConstructorValueGenericMapOfStrings serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = ConstructorValueGenericMapOfStrings(
            mapOf(
                "DataSize" to 2048,
                "ErrorCode" to 201
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorValueGenericMapOfStrings<Int> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorGenericInlineMapOfStringsTest")
    fun `ConstructorGenericInlineMapOfStrings serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = ConstructorGenericInlineMapOfStrings(
            mapOf(
                "DataSize" to 2048,
                "ErrorCode" to 201
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorGenericInlineMapOfStrings<String, Int> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorKeyGenericInlineMapOfStringsTest")
    fun `ConstructorKeyGenericInlineMapOfStrings serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = ConstructorKeyGenericInlineMapOfStrings(
            mapOf(
                "DataSize" to 2048,
                "ErrorCode" to 201
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorKeyGenericInlineMapOfStrings<String> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorValueGenericInlineMapOfStringsTest")
    fun `ConstructorValueGenericInlineMapOfStrings serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = ConstructorValueGenericInlineMapOfStrings(
            mapOf(
                "DataSize" to 2048,
                "ErrorCode" to 201
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorValueGenericInlineMapOfStrings<Int> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("FieldGenericMapOfStringsWithDefaultTest")
    fun `FieldGenericMapOfStringsWithDefault serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = FieldGenericMapOfStringsWithDefault<String, Int>()
        bean.map = mapOf(
            "DataSize" to 2048,
            "ErrorCode" to 201
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldGenericMapOfStringsWithDefault<String, Int> = SimpleXml.deserialize(xml)
        assertEquals(bean.map, deserializedBean.map)
    }
}