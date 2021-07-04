package ua.vald_zx.simplexml.ksp.processor.privitives

import org.junit.Test
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.processor.privitives.beans.ModuleInitializer
import ua.vald_zx.simplexml.ksp.processor.privitives.beans.StringTag
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class PrimitivesTest {
    @BeforeTest
    fun init() {
        ModuleInitializer.setup()
    }

    val xml = "<RootString><Tag>data</Tag></RootString>"

    @Test
    fun StringSerializationTagTest() {
        val obj = StringTag("data")
        val serializationResult = SimpleXml.serialize(obj)
        assertEquals(serializationResult, xml)
    }

    @Test
    fun StringDeserializationTagTest() {
        val obj = SimpleXml.deserialize<StringTag>(xml)
        assertEquals(StringTag("data"), obj)
    }
}