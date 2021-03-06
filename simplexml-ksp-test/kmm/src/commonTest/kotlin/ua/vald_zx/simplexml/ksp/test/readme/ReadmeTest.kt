package ua.vald_zx.simplexml.ksp.test.readme

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.TestSerializersEnrolment
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadmeTest {

    @BeforeTest
    fun init() {
        TestSerializersEnrolment.enrol()
    }

    @Test
    @JsName("PackageDtoTest")
    fun `PackageDto serialization test`() {
        SimpleXml.pretty = true
        val bean = PackageDto(
            serviceName = "GET_INFO",
            token = "S290bGluIGlzIGF3ZXNvbWU=",
            location = "Ukraine, Kharkiv",
            latitude = 50.004977,
            longitude = 36.231117
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: PackageDto = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("StringResourcesTest")
    fun `StringResources serialization test`() {
        SimpleXml.pretty = true
        val bean = StringResources(
            listOf(
                StringResource("appName", "The best app"),
                StringResource("greetings", "Hello!")
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: StringResources = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("StringResourcesMapTest")
    fun `StringResourcesMap serialization test`() {
        SimpleXml.pretty = true
        val bean = StringResourcesMap(
            mapOf(
                "appName" to "The best app",
                "greetings" to "Hello!"
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: StringResourcesMap = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }
}