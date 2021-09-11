package ua.vald_zx.simplexml.ksp.test.converter

import ua.vald_zx.simplexml.ksp.Convert
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.TestSerializersEnrolment
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class WithConverterField {
    @field:[Element Convert(TestStringConverter::class)]
    var tag: String = ""
}

class FieldConverterTest {

    @BeforeTest
    fun init() {
        TestSerializersEnrolment.enrol()
        SimpleXml.pretty = false
    }

    @Test
    @JsName("WithConverterFieldTest")
    fun `WithConverterField serialize deserialize test`() {
        val bean = WithConverterField()
        bean.tag = "WithConverterField"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: WithConverterField = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    @JsName("WithConverterFieldDeserializeTest")
    fun `WithConverterField deserialize test`() {
        val deserializedBean: WithConverterField =
            SimpleXml.deserialize("<WithConverterField><tag>~~~Secret~~~</tag></WithConverterField>")
        assertEquals("Secret", deserializedBean.tag)
    }

    @Test
    @JsName("WithConverterFieldSerializeTest")
    fun `WithConverterField serialize test`() {
        val bean = WithConverterField()
        bean.tag = "Secret"
        val xml = SimpleXml.serialize(bean)
        assertEquals(xml, "<WithConverterField><tag>~~~Secret~~~</tag></WithConverterField>")
    }
}