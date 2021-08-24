package ua.vald_zx.simplexml.ksp.test.converter

import ua.vald_zx.simplexml.ksp.Convert
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.sample.custompackage.SampleSerializersEnrolment
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
    }

    @Test
    fun `WithConverterConstructorFieldTest serialize deserialize test`() {
        val bean = WithConverterConstructorField("WithConverterConstructorField")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: WithConverterConstructorField = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `WithConverterConstructorFieldTest deserialize test`() {
        val deserializedBean: WithConverterConstructorField =
            SimpleXml.deserialize("<WithConverterConstructorField><tag>~~~Secret~~~</tag></WithConverterConstructorField>")
        assertEquals("Secret", deserializedBean.tag)
    }

    @Test
    fun `WithConverterConstructorFieldTest serialize test`() {
        val bean = WithConverterConstructorField("Secret")
        val xml = SimpleXml.serialize(bean)
        assertEquals(xml, "<WithConverterConstructorField><tag>~~~Secret~~~</tag></WithConverterConstructorField>")
    }
}