package ua.vald_zx.simplexml.ksp.test.converter

import ua.vald_zx.simplexml.ksp.Convert
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.TestSerializersEnrolment
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

data class MultipleConvertersConstructorField(
    @field:[Element Convert(TestStringConverter::class)]
    var tag1: String,
    @field:[Element Convert(TestDoubleConverter::class)]
    var tag2: Double,
    @field:[Element]
    var tag3: String,
    @field:[Element]
    var tag4: Double
)

class MultipleConvertersConstructorFieldTest {

    @BeforeTest
    fun init() {
        TestSerializersEnrolment.enrol()
        SimpleXml.pretty = false
    }

    @Test
    @JsName("MultipleConvertersConstructorFieldTest")
    fun `MultipleConvertersConstructorField serialize deserialize test`() {
        val bean = MultipleConvertersConstructorField(
            "Value1",
            34.0,
            "Value1",
            34.0,
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: MultipleConvertersConstructorField = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("MultipleConvertersConstructorFieldDeserializeTest")
    fun `MultipleConvertersConstructorField deserialize test`() {
        val deserializedBean: MultipleConvertersConstructorField =
            SimpleXml.deserialize("<MultipleConvertersConstructorField><tag1>~~~Secret~~~</tag1><tag2>28</tag2><tag3>Secret</tag3><tag4>14</tag4></MultipleConvertersConstructorField>")
        assertEquals("Secret", deserializedBean.tag1)
        assertEquals(14.0, deserializedBean.tag2)
        assertEquals("Secret", deserializedBean.tag3)
        assertEquals(14.0, deserializedBean.tag4)
    }

    @Test
    @JsName("MultipleConvertersConstructorFieldSerializeTest")
    fun `MultipleConvertersConstructorField serialize test`() {
        val bean = MultipleConvertersConstructorField("Secret", 14.0, "Secret", 14.0)
        val xml = SimpleXml.serialize(bean)
        assertEquals(
            xml,
            "<MultipleConvertersConstructorField><tag1>~~~Secret~~~</tag1><tag2>28</tag2><tag3>Secret</tag3><tag4>14</tag4></MultipleConvertersConstructorField>"
        )
    }
}