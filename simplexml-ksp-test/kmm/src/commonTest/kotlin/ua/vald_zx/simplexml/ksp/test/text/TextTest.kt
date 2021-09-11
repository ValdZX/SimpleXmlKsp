package ua.vald_zx.simplexml.ksp.test.text

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.exception.DeserializeException
import ua.vald_zx.simplexml.ksp.test.custompackage.TestSerializersEnrolment
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TextTest {

    @BeforeTest
    fun init() {
        TestSerializersEnrolment.enrol()
    }

    @Test
    @JsName("ConstructorTextTest")
    fun `ConstructorText serialize deserialize test`() {
        val bean = ConstructorText("RootText")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorText = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorTextWithAttributeTest")
    fun `ConstructorTextWithAttribute serialize deserialize test`() {
        val bean = ConstructorTextWithAttribute("RootText", "AttrText")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorTextWithAttribute = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorNullableTextTest")
    fun `ConstructorNullableText serialize deserialize test`() {
        val bean = ConstructorNullableText("RootText")
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorNullableText = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("ConstructorNullableTextNullValueTest")
    fun `ConstructorNullableText null value serialize deserialize test`() {
        val bean = ConstructorNullableText(null)
        val xml = SimpleXml.serialize(bean)
        println(xml)
        assertFailsWith<DeserializeException> {
            val deserializedBean: ConstructorNullableText = SimpleXml.deserialize(xml)
            assertEquals(bean, deserializedBean)
        }
    }

    @Test
    @JsName("ConstructorNullableTextNotRequiredNullValueTest")
    fun `ConstructorNullableTextNotRequired null value serialize deserialize test`() {
        val bean = ConstructorNullableTextNotRequired(null)
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ConstructorNullableTextNotRequired = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("FieldTextTest")
    fun `FieldText serialize deserialize test`() {
        val bean = FieldText()
        bean.text = "RootText"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldText = SimpleXml.deserialize(xml)
        assertEquals(bean.text, deserializedBean.text)
    }

    @Test
    @JsName("FieldNullableTextTest")
    fun `FieldNullableText serialize deserialize test`() {
        val bean = FieldNullableText()
        bean.text = "RootText"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldNullableText = SimpleXml.deserialize(xml)
        assertEquals(bean.text, deserializedBean.text)
    }

    @Test
    @JsName("FieldNullableTextNullValueTest")
    fun `FieldNullableText null value serialize deserialize test`() {
        val bean = FieldNullableText()
        bean.text = null
        val xml = SimpleXml.serialize(bean)
        println(xml)
        assertFailsWith<DeserializeException> {
            val deserializedBean: FieldNullableText = SimpleXml.deserialize(xml)
            assertEquals(bean.text, deserializedBean.text)
        }
    }

    @Test
    @JsName("FieldNullableNotRequiredTextTest")
    fun `FieldNullableNotRequiredText serialize deserialize test`() {
        val bean = FieldNullableNotRequiredText()
        bean.text = null
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: FieldNullableNotRequiredText = SimpleXml.deserialize(xml)
        assertEquals(bean.text, deserializedBean.text)
    }
}