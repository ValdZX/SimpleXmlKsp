package ua.vald_zx.simplexml.ksp.test.converter

import ua.vald_zx.simplexml.ksp.Convert
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.sample.custompackage.SampleModuleInitializer
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
        SampleModuleInitializer.setup()
    }

    @Test
    fun `WithConverterField serialize deserialize test`() {
        val bean = WithConverterField()
        bean.tag = "WithConverterField"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: WithConverterField = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    fun `WithConverterField deserialize test`() {
        val deserializedBean: WithConverterField =
            SimpleXml.deserialize("<WithConverterField><tag>~~~Secret~~~</tag></WithConverterField>")
        assertEquals("Secret", deserializedBean.tag)
    }

    @Test
    fun `WithConverterField serialize test`() {
        val bean = WithConverterField()
        bean.tag = "Secret"
        val xml = SimpleXml.serialize(bean)
        assertEquals(xml, "<WithConverterField><tag>~~~Secret~~~</tag></WithConverterField>")
    }
}