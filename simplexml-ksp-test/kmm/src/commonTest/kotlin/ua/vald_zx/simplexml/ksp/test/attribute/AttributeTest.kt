package ua.vald_zx.simplexml.ksp.test.attribute

import ua.vald_zx.simplexml.ksp.SimpleXml
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import ua.vald_zx.simplexml.ksp.test.custompackage.SampleSerializersEnrolment
import kotlin.js.JsName

class AttributeTest {

    @BeforeTest
    fun init() {
        SampleSerializersEnrolment.enrol()
    }

    @Test
    @JsName("RootAttributeTest")
    fun `RootAttribute serialize deserialize test`() {
        val bean = RootAttribute("RootAttribute")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: RootAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("PathAttributeTest")
    fun `PathAttribute serialize deserialize test`() {
        val bean = PathAttribute("PathAttribute")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: PathAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("TagAttributeTest")
    fun `TagAttribute serialize deserialize test`() {
        val bean = TagAttribute("TagAttributeATTR", "TagAttributeTAG")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: TagAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ListAttributeTest")
    fun `ListAttribute serialize deserialize test`() {
        val bean = ListAttribute("ListAttribute", listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ListAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("NullableTagAttributeTest")
    fun `NullableTagAttribute serialize deserialize test`() {
        val bean = NullableTagAttribute("NullableTagAttributeATTR", "NullableTagAttributeTAG")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NullableTagAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("NullableTagAttributeEmptyTagTest")
    fun `NullableTagAttribute with empty tag serialize deserialize test`() {
        val bean = NullableTagAttribute("NullableTagAttributeATTR")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NullableTagAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean.attr, deserializedBean.attr)
        assertEquals(deserializedBean.tag, "")
    }
}