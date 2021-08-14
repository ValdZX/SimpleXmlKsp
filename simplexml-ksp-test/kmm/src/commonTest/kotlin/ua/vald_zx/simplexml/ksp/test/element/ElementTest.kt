package ua.vald_zx.simplexml.ksp.test.element

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.exception.DeserializeException
import ua.vald_zx.simplexml.ksp.exception.SerializeException
import ua.vald_zx.simplexml.ksp.sample.custompackage.SampleModuleInitializer
import kotlin.test.*

class ElementTest {

    @BeforeTest
    fun init() {
        SampleModuleInitializer.setup()
    }

    @Test
    fun RequiredConstructorFieldTest() {
        val bean = RequiredConstructorField("RequiredConstructorField")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: RequiredConstructorField = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `RequiredConstructorFieldTest deserialize empty single tag`() {
        assertFailsWith(DeserializeException::class) {
            SimpleXml.deserialize<RequiredConstructorField>("<RequiredConstructorField/>")
        }
    }

    @Test
    fun `RequiredConstructorFieldTest deserialize empty tag`() {
        assertFailsWith(DeserializeException::class) {
            SimpleXml.deserialize<RequiredConstructorField>("<RequiredConstructorField><RequiredConstructorField/>")
        }
    }

    @Test
    fun `RequiredConstructorFieldTest deserialize empty tag value`() {
        val bean =
            SimpleXml.deserialize<RequiredConstructorField>("<RequiredConstructorField><tag></tag><RequiredConstructorField/>")
        assertEquals(bean.tag, "")
    }

    @Test
    fun RequiredConstructorNullableFieldTest() {
        val bean = RequiredConstructorNullableField("RequiredConstructorNullableField")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: RequiredConstructorNullableField = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `RequiredConstructorNullableFieldTest serialize null value`() {
        val bean = RequiredConstructorNullableField(null)
        assertFailsWith(SerializeException::class) {
            SimpleXml.serialize(bean)
        }
    }

    @Test
    fun `RequiredConstructorNullableFieldTest null value`() {
        assertFailsWith(DeserializeException::class) {
            SimpleXml.deserialize<RequiredConstructorNullableField>("<RequiredConstructorNullableField/>")
        }
    }

    @Test
    fun NotRequiredConstructorNullableFieldTest() {
        val bean = NotRequiredConstructorNullableField("NotRequiredConstructorNullableField")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NotRequiredConstructorNullableField = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `NotRequiredConstructorNullableFieldTest null test`() {
        val xml = "<NotRequiredConstructorNullableField/>"
        val deserializedBean: NotRequiredConstructorNullableField = SimpleXml.deserialize(xml)
        assertEquals(null, deserializedBean.tag)
    }

    @Test
    fun RequiredConstructorFieldWithDefaultTest() {
        val bean = RequiredConstructorFieldWithDefault("RequiredConstructorFieldWithDefault")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: RequiredConstructorFieldWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `RequiredConstructorFieldWithDefaultTest default`() {
        val bean = RequiredConstructorFieldWithDefault()
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: RequiredConstructorFieldWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun NotRequiredConstructorFieldWithDefaultTest() {
        val bean = NotRequiredConstructorFieldWithDefault("NotRequiredConstructorFieldWithDefault")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NotRequiredConstructorFieldWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `NotRequiredConstructorFieldWithDefaultTest deserealize empty`() {
        val bean =
            SimpleXml.deserialize<NotRequiredConstructorFieldWithDefault>("<RequiredConstructorFieldWithDefault/>")
        assertEquals(bean.tag, "Default")
    }

    @Test
    fun RequiredConstructorNullableFieldWithDefaultTest() {
        val bean = RequiredConstructorNullableFieldWithDefault("RequiredConstructorNullableFieldWithDefault")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: RequiredConstructorNullableFieldWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    fun `RequiredConstructorNullableFieldWithDefaultTest serialize value null`() {
        val bean = RequiredConstructorNullableFieldWithDefault(null)
        assertFailsWith(SerializeException::class) {
            SimpleXml.serialize(bean)
        }
    }

    @Test
    fun `RequiredConstructorNullableFieldWithDefaultTest deserialize value null`() {
        assertFailsWith(DeserializeException::class) {
            SimpleXml.deserialize<RequiredConstructorNullableFieldWithDefault>("<RequiredConstructorNullableFieldWithDefault/>")
        }
    }

    @Test
    fun RequiredFieldTest() {
        val bean = RequiredField()
        bean.tag = "RequiredField"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: RequiredField = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    fun `RequiredFieldTest deserialize empty tag`() {
        assertFailsWith(DeserializeException::class) {
            SimpleXml.deserialize<RequiredField>("<RequiredField/>")
        }
    }

    @Test
    fun NotRequiredFieldTest() {
        val bean = NotRequiredField()
        bean.tag = "NotRequiredField"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NotRequiredField = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    fun `NotRequiredFieldTest deserialize empty tag`() {
        val bean = SimpleXml.deserialize<NotRequiredField>("<NotRequiredField/>")
        assertEquals(bean.tag, "")
    }

    @Test
    fun RequiredNullableFieldTest() {
        val bean = RequiredNullableField()
        bean.tag = "RequiredNullableField"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: RequiredNullableField = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    fun `RequiredNullableFieldTest serialize null`() {
        val bean = RequiredNullableField()
        bean.tag = null
        assertFailsWith(SerializeException::class) {
            SimpleXml.serialize(bean)
        }
    }

    @Test
    fun `RequiredNullableFieldTest deserialize empty tag`() {
        assertFailsWith(DeserializeException::class) {
            SimpleXml.deserialize<RequiredNullableField>("<RequiredNullableField/>")
        }
    }

    @Test
    fun NotRequiredNullableFieldTest() {
        val bean = NotRequiredNullableField()
        bean.tag = "NotRequiredNullableField"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NotRequiredNullableField = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    fun `NotRequiredNullableFieldTest serialize null`() {
        val bean = NotRequiredNullableField()
        bean.tag = null
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NotRequiredNullableField = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    fun `NotRequiredNullableFieldTest deserialize empty tag`() {
        val deserializedBean: NotRequiredNullableField = SimpleXml.deserialize("<NotRequiredNullableField/>")
        assertNull(deserializedBean.tag)
    }

    @Test
    fun RequiredFieldWithDefaultTest() {
        val bean = RequiredFieldWithDefault()
        bean.tag = "RequiredFieldWithDefault"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: RequiredFieldWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    fun NotRequiredFieldWithDefaultTest() {
        val bean = NotRequiredFieldWithDefault()
        bean.tag = "NotRequiredFieldWithDefault"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NotRequiredFieldWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    fun `NotRequiredFieldWithDefaultTest serialize empty`() {
        val bean = NotRequiredFieldWithDefault()
        bean.tag = ""
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NotRequiredFieldWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    fun `NotRequiredFieldWithDefaultTest deserialize empty tag`() {
        val deserializedBean: NotRequiredFieldWithDefault = SimpleXml.deserialize("<NotRequiredFieldWithDefault/>")
        assertEquals("Default", deserializedBean.tag)
    }

    @Test
    fun RequiredNullableFieldWithDefaultTest() {
        val bean = RequiredNullableFieldWithDefault()
        bean.tag = "RequiredNullableFieldWithDefault"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: RequiredNullableFieldWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    fun `RequiredNullableFieldWithDefaultTest value null`() {
        val bean = RequiredNullableFieldWithDefault()
        bean.tag = null
        assertFailsWith(SerializeException::class) {
            SimpleXml.serialize(bean)
        }
    }

    @Test
    fun `RequiredNullableFieldWithDefaultTest empty tag`() {
        assertFailsWith(DeserializeException::class) {
            SimpleXml.deserialize<RequiredNullableFieldWithDefault>("<RequiredNullableFieldWithDefault/>")
        }
    }

    @Test
    fun NotRequiredNullableFieldWithDefaultTest() {
        val bean = NotRequiredNullableFieldWithDefault()
        bean.tag = "NotRequiredNullableFieldWithDefault"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: NotRequiredNullableFieldWithDefault = SimpleXml.deserialize(xml)
        assertEquals(bean.tag, deserializedBean.tag)
    }

    @Test
    fun `NotRequiredNullableFieldWithDefaultTest deserialize empty tag`() {
        val deserializedBean: NotRequiredNullableFieldWithDefault =
            SimpleXml.deserialize("<NotRequiredNullableFieldWithDefault/>")
        assertEquals("Default", deserializedBean.tag)
    }
}