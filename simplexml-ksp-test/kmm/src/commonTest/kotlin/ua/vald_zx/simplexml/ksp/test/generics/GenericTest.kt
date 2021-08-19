package ua.vald_zx.simplexml.ksp.test.generics

import ua.vald_zx.simplexml.ksp.SimpleXml
import kotlin.test.BeforeTest
import ua.vald_zx.simplexml.ksp.sample.custompackage.SampleModuleInitializer
import kotlin.test.Test
import kotlin.test.assertEquals

class GenericTest {

    @BeforeTest
    fun init() {
        SampleModuleInitializer.setup()
    }

    @Test
    fun `GenericDataClass serialize deserialize test`() {
        val bean = GenericDataClass("String1", 25f)
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: GenericDataClass<String, Float> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }
}