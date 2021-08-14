package ua.vald_zx.simplexml.ksp.test.attribute

import ua.vald_zx.simplexml.ksp.SimpleXml
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import ua.vald_zx.simplexml.ksp.sample.custompackage.SampleModuleInitializer

class AttributeTest {

    @BeforeTest
    fun init() {
        SampleModuleInitializer.setup()
    }

    @Test
    fun `RootAttribute serialize deserialize test`() {
        val bean = RootAttribute("RootAttribute")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: RootAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `PathAttribute serialize deserialize test`() {
        val bean = PathAttribute("PathAttribute")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: PathAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `TagAttribute serialize deserialize test`() {
        val bean = TagAttribute("TagAttributeATTR", "TagAttributeTAG")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: TagAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

//    @Test
//    fun `SubTagPathAttribute serialize deserialize test`() {
//        val bean = SubTagPathAttribute("SubTagPathAttributeATTR", "SubTagPathAttributeTAG")
//        val xml = SimpleXml.serialize(bean)
//        println(xml)
//        val deserializedBean: SubTagPathAttribute = SimpleXml.deserialize(xml)
//        assertEquals(bean, deserializedBean)
//    }

    @Test
    fun `ListAttribute serialize deserialize test`() {
        val bean = ListAttribute("ListAttribute", listOf("Value1", "Value2", "Value3"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ListAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }
}