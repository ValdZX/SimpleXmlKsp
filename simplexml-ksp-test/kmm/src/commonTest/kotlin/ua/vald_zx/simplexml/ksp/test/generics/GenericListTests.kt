package ua.vald_zx.simplexml.ksp.test.generics

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.TestSerializersEnrolment
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GenericListTests {

    @BeforeTest
    fun init() {
        TestSerializersEnrolment.enrol()
    }

    @Test
    @JsName("GenericConstructorArgListOfStringsTest")
    fun `GenericConstructorArgListOfStrings serialize deserialize test`() {
        val bean = GenericConstructorArgListOfStrings(mutableListOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: GenericConstructorArgListOfStrings<String> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("GenericNullableConstructorArgListOfStringsTest")
    fun `GenericNullableConstructorArgListOfStrings serialize deserialize test`() {
        val bean = GenericNullableConstructorArgListOfStrings(mutableListOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: GenericNullableConstructorArgListOfStrings<String> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("GenericNullableFieldListOfStringsTest")
    fun `GenericNullableFieldListOfStrings serialize deserialize test`() {
        val bean = GenericNullableFieldListOfStrings<String>()
        bean.list = mutableListOf("Value1", "Value2", "Value3")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: GenericNullableFieldListOfStrings<String> = SimpleXml.deserialize(xml)
        assertEquals(bean.list, deserializedBean.list)
    }

    @Test
    @JsName("GenericNullableFieldMutableListOfStringsTest")
    fun `GenericNullableFieldMutableListOfStrings serialize deserialize test`() {
        val bean = GenericNullableFieldMutableListOfStrings<String>()
        bean.list = mutableListOf("Value1", "Value2", "Value3")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: GenericNullableFieldMutableListOfStrings<String> = SimpleXml.deserialize(xml)
        assertEquals(bean.list, deserializedBean.list)
    }
}