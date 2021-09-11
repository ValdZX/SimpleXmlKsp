package ua.vald_zx.simplexml.ksp.test.converter

import ua.vald_zx.simplexml.ksp.Convert
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.SampleSerializersEnrolment
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

data class WithConverterConstructorField(
    @field:[Element Convert(TestStringConverter::class)]
    var tag: String
)

class FieldInConstructorConverterTest {

    @BeforeTest
    fun init() {
        SampleSerializersEnrolment.enrol()
        SimpleXml.pretty = false
    }

    @Test
    @JsName("WithConverterConstructorFieldTest")
    fun `WithConverterConstructorField serialize deserialize test`() {
        val bean = WithConverterConstructorField("WithConverterConstructorField")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: WithConverterConstructorField = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("WithConverterConstructorFieldDeserializeTest")
    fun `WithConverterConstructorField deserialize test`() {
        val deserializedBean: WithConverterConstructorField =
            SimpleXml.deserialize("<WithConverterConstructorField><tag>~~~Secret~~~</tag></WithConverterConstructorField>")
        assertEquals("Secret", deserializedBean.tag)
    }

    @Test
    @JsName("WithConverterConstructorFieldSerializeTest")
    fun `WithConverterConstructorFieldTest serialize test`() {
        val bean = WithConverterConstructorField("Secret")
        val xml = SimpleXml.serialize(bean)
        assertEquals(xml, "<WithConverterConstructorField><tag>~~~Secret~~~</tag></WithConverterConstructorField>")
    }
}