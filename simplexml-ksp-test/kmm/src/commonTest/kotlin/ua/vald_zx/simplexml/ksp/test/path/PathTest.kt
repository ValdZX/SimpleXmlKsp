package ua.vald_zx.simplexml.ksp.test.path

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.TestSerializersEnrolment
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PathTest {

    @BeforeTest
    fun init() {
        TestSerializersEnrolment.enrol()
    }

    @Test
    @JsName("ElementWithOneLayerPathTest")
    fun `ElementWithOneLayerPath serialize deserialize test`() {
        val bean = ElementWithOneLayerPath("ElementWithOneLayerPath")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ElementWithOneLayerPath = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ElementWithTwoLayerPathTest")
    fun `ElementWithTwoLayerPath serialize deserialize test`() {
        val bean = ElementWithTwoLayerPath("ElementWithTwoLayerPath")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ElementWithTwoLayerPath = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ElementWithThreeLayerPathTest")
    fun `ElementWithThreeLayerPath serialize deserialize test`() {
        val bean = ElementWithThreeLayerPath("ElementWithThreeLayerPath")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ElementWithThreeLayerPath = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("TwoElementsOnFirstLayerPathTest")
    fun `TwoElementsOnFirstLayerPath serialize deserialize test`() {
        val bean = TwoElementsOnFirstLayerPath("Data1", "Data2")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: TwoElementsOnFirstLayerPath = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("TwoElementsOnDiffLayersPathTest")
    fun `TwoElementsOnDiffLayersPath serialize deserialize test`() {
        val bean = TwoElementsOnDiffLayersPath("Data1", "Data2")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: TwoElementsOnDiffLayersPath = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }
}