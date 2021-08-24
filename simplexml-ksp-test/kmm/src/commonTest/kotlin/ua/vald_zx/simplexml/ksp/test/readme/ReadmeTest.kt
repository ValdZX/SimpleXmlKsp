package ua.vald_zx.simplexml.ksp.test.readme

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.sample.custompackage.SampleModuleInitializer
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadmeTest {

    @BeforeTest
    fun init() {
        SampleModuleInitializer.setup()
    }

    @Test
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
}