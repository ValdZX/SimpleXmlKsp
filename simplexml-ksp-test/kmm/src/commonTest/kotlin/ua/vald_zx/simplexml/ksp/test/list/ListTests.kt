package ua.vald_zx.simplexml.ksp.test.list

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.TestSerializersEnrolment
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ListTests {

    @BeforeTest
    fun init() {
        TestSerializersEnrolment.enrol()
    }

    @Test
    @JsName("ConstructorFieldListOfStringsTest")
    fun `ConstructorFieldListOfStrings serialize deserialize test`() {
        val bean = ConstructorFieldListOfStrings(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldListOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorFieldListOfStringsNullableTest")
    fun `ConstructorFieldListOfStringsNullable serialize deserialize test`() {
        val bean = ConstructorFieldListOfStringsNullable(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldListOfStringsNullable = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorInlineFieldListOfStringsTest")
    fun `ConstructorInlineFieldListOfStrings serialize deserialize test`() {
        val bean = ConstructorInlineFieldListOfStrings(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorInlineFieldListOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorFieldListOfStringsWithoutNameTest")
    fun `ConstructorFieldListOfStringsWithoutName serialize deserialize test`() {
        val bean = ConstructorFieldListOfStringsWithoutName(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldListOfStringsWithoutName = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorFieldListOfStringsWithoutEntryTest")
    fun `ConstructorFieldListOfStringsWithoutEntry serialize deserialize test`() {
        val bean = ConstructorFieldListOfStringsWithoutEntry(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldListOfStringsWithoutEntry = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorInlineFieldListOfStringsWithoutEntryTest")
    fun `ConstructorInlineFieldListOfStringsWithoutEntry serialize deserialize test`() {
        val bean = ConstructorInlineFieldListOfStringsWithoutEntry(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorInlineFieldListOfStringsWithoutEntry = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorFieldListOfObjectsTest")
    fun `ConstructorFieldListOfObjects serialize deserialize test`() {
        val bean = ConstructorFieldListOfObjects(
            listOf(
                ListItem("Value1", 1),
                ListItem("Value2", 2),
                ListItem("Value3", 3),
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldListOfObjects = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorInlineFieldListOfObjectsTest")
    fun `ConstructorInlineFieldListOfObjects serialize deserialize test`() {
        val bean = ConstructorInlineFieldListOfObjects(
            listOf(
                ListItem("Value1", 1),
                ListItem("Value2", 2),
                ListItem("Value3", 3),
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorInlineFieldListOfObjects = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorFieldListOfObjectsWithConvertTest")
    fun `ConstructorFieldListOfObjectsWithConvert serialize deserialize test`() {
        val bean = ConstructorFieldListOfObjectsWithConvert(
            listOf(
                ListItem("Value1", 1),
                ListItem("Value2", 2),
                ListItem("Value3", 3),
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldListOfObjectsWithConvert = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("FieldListOfStringsTest")
    fun `FieldListOfStrings serialize deserialize test`() {
        val bean = FieldListOfStrings(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldListOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean.list, deserializedBean.list)
    }

    @Test
    @JsName("FieldInlineListOfStringsTest")
    fun `FieldInlineListOfStrings serialize deserialize test`() {
        val bean = FieldInlineListOfStrings(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldInlineListOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean.list, deserializedBean.list)
    }

    @Test
    @JsName("ConstructorFieldMutableListOfStringsTest")
    fun `ConstructorFieldMutableListOfStrings serialize deserialize test`() {
        val bean = ConstructorFieldMutableListOfStrings(mutableListOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldMutableListOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorFieldMutableListOfStringsWithDefaultTest")
    fun `ConstructorFieldMutableListOfStringsWithDefault serialize deserialize test`() {
        val bean = ConstructorFieldMutableListOfStringsWithDefault(mutableListOf("Value4", "Value5", "Value6"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldMutableListOfStringsWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorFieldMutableListOfStringsWithDefaultTest_Default")
    fun `ConstructorFieldMutableListOfStringsWithDefault serialize deserialize test default`() {
        val bean = ConstructorFieldMutableListOfStringsWithDefault()
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldMutableListOfStringsWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorFieldMutableListOfStringsWithAttributeTest")
    fun `ConstructorFieldMutableListOfStringsWithAttribute serialize deserialize test`() {
        val bean = ConstructorFieldMutableListOfStringsWithAttribute(mutableListOf("Value1", "Value2", "Value3"), 3)
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldMutableListOfStringsWithAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorFieldListOfStringsNullableWithAttributeTest")
    fun `ConstructorFieldListOfStringsNullableWithAttribute serialize deserialize test`() {
        val bean = ConstructorFieldListOfStringsNullableWithAttribute(listOf("Value1", "Value2", "Value3"), 3)
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldListOfStringsNullableWithAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("FieldMutableListOfStringsTest")
    fun `FieldMutableListOfStrings serialize deserialize test`() {
        val bean = FieldMutableListOfStrings()
        bean.list = mutableListOf("Value1", "Value2", "Value3")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldMutableListOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean.list, deserializedBean.list)
    }

    @Test
    @JsName("NullableFieldMutableListOfStringsTest")
    fun `NullableFieldMutableListOfStrings serialize deserialize test`() {
        val bean = NullableFieldMutableListOfStrings()
        bean.list = mutableListOf("Value1", "Value2", "Value3")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NullableFieldMutableListOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean.list, deserializedBean.list)
    }

    @Test
    @JsName("NullableFieldMutableListOfObjectsTest")
    fun `NullableFieldMutableListOfObjects serialize deserialize test`() {
        val bean = NullableFieldMutableListOfObjects()
        bean.list = mutableListOf(
            ListItem("Value1", 1),
            ListItem("Value2", 2),
            ListItem("Value3", 3),
        )

        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NullableFieldMutableListOfObjects = SimpleXml.deserialize(xml)
        assertEquals(bean.list, deserializedBean.list)
    }

    @Test
    @JsName("FieldMutableListOfObjectsTest")
    fun `FieldMutableListOfObjects serialize deserialize test`() {
        val bean = FieldMutableListOfObjects()
        bean.list = mutableListOf(
            ListItem("Value1", 1),
            ListItem("Value2", 2),
            ListItem("Value3", 3),
        )

        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldMutableListOfObjects = SimpleXml.deserialize(xml)
        assertEquals(bean.list, deserializedBean.list)
    }

    @Test
    @JsName("FieldMutableListOfObjectsNotRequiredTest")
    fun `FieldMutableListOfObjectsNotRequired serialize deserialize test`() {
        val bean = FieldMutableListOfObjectsNotRequired()
        bean.list = mutableListOf(
            ListItem("Value1", 1),
            ListItem("Value2", 2),
            ListItem("Value3", 3),
        )

        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldMutableListOfObjectsNotRequired = SimpleXml.deserialize(xml)
        assertEquals(bean.list, deserializedBean.list)
    }
}