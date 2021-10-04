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
    @JsName("AndroidStringResourcesChildTest")
    fun `AndroidStringResourcesChild serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = AndroidStringResourcesChild(
            resources = mapOf(
                "appName" to "The best app",
                "greetings" to "Hello!"
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: AndroidStringResourcesChild = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("AndroidStringResourcesTest")
    fun `AndroidStringResources serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = AndroidStringResources(
            resources = mapOf(
                "appName" to "The best app",
                "greetings" to "Hello!"
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: AndroidStringResources = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("FieldAndroidStringResourcesChildTest")
    fun `FieldAndroidStringResourcesChild serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = FieldAndroidStringResourcesChild()
        bean.resources = mapOf(
            "appName" to "The best app",
            "greetings" to "Hello!"
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldAndroidStringResourcesChild = SimpleXml.deserialize(xml)
        assertEquals(bean.resources, deserializedBean.resources)
        assertEquals(bean.ns, deserializedBean.ns)
    }

    @Test
    @JsName("FieldAndroidStringResourcesTest")
    fun `FieldAndroidStringResources serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = FieldAndroidStringResources()
        bean.resources = mapOf(
            "appName" to "The best app",
            "greetings" to "Hello!"
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldAndroidStringResources = SimpleXml.deserialize(xml)
        assertEquals(bean.resources, deserializedBean.resources)
        assertEquals(bean.ns, deserializedBean.ns)
    }

    @Test
    @JsName("NullableFieldAndroidStringResourcesTest")
    fun `NullableFieldAndroidStringResources serialize deserialize test`() {
        SimpleXml.pretty = true
        val bean = NullableFieldAndroidStringResources()
        bean.resources = mapOf(
            "appName" to "The best app",
            "greetings" to "Hello!"
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NullableFieldAndroidStringResources = SimpleXml.deserialize(xml)
        assertEquals(bean.resources, deserializedBean.resources)
        assertEquals(bean.ns, deserializedBean.ns)
    }
}