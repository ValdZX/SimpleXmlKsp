package ua.vald_zx.simplexml.ksp.test.extension

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.SampleSerializersEnrolment
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionTest {

    @BeforeTest
    fun init() {
        SampleSerializersEnrolment.enrol()
    }

    @Test
    @JsName("ExtBeanSerializerTest")
    fun `ExtBeanSerializer test`() {
        SimpleXml.pretty = true
        val bean = ExtBean<String, Float, Char>()
        bean.somObject1 = "String"
        bean.somObject2 = 27f
        bean.somObject3 = 3f
        bean.somObject4 = '~'
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ExtBean<String, Float, Char> = SimpleXml.deserialize(xml)
        assertEquals(bean.somObject1, deserializedBean.somObject1)
        assertEquals(bean.somObject2, deserializedBean.somObject2)
        assertEquals(bean.somObject3, deserializedBean.somObject3)
        assertEquals(bean.somObject4, deserializedBean.somObject4)
    }
}