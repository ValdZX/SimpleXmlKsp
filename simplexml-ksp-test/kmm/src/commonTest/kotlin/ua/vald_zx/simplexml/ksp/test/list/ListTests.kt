package ua.vald_zx.simplexml.ksp.test.list

import ua.vald_zx.simplexml.ksp.SimpleXml
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import ua.vald_zx.simplexml.ksp.sample.custompackage.SampleSerializersEnrolment

class ListTests {

    @BeforeTest
    fun init() {
        SampleSerializersEnrolment.enrol()
    }

    @Test
    fun `ConstructorFieldListOfStrings serialize deserialize test`() {
        val bean = ConstructorFieldListOfStrings(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldListOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `ConstructorInlineFieldListOfStrings serialize deserialize test`() {
        val bean = ConstructorInlineFieldListOfStrings(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorInlineFieldListOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `ConstructorFieldListOfStringsWithoutName serialize deserialize test`() {
        val bean = ConstructorFieldListOfStringsWithoutName(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldListOfStringsWithoutName = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `ConstructorFieldListOfStringsWithoutEntry serialize deserialize test`() {
        val bean = ConstructorFieldListOfStringsWithoutEntry(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorFieldListOfStringsWithoutEntry = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `ConstructorInlineFieldListOfStringsWithoutEntry serialize deserialize test`() {
        val bean = ConstructorInlineFieldListOfStringsWithoutEntry(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorInlineFieldListOfStringsWithoutEntry = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
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
    fun `FieldListOfStrings serialize deserialize test`() {
        val bean = FieldListOfStrings(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldListOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean.list, deserializedBean.list)
    }

    @Test
    fun `FieldInlineListOfStrings serialize deserialize test`() {
        val bean = FieldInlineListOfStrings(listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldInlineListOfStrings = SimpleXml.deserialize(xml)
        assertEquals(bean.list, deserializedBean.list)
    }
}