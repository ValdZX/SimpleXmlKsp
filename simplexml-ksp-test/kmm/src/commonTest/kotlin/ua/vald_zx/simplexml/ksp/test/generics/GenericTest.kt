package ua.vald_zx.simplexml.ksp.test.generics

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.test.custompackage.TestSerializersEnrolment
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GenericTest {

    @BeforeTest
    fun init() {
        SimpleXml.pretty = true
        TestSerializersEnrolment.enrol()
    }

    @Test
    @JsName("GenericDataClassTest")
    fun `GenericDataClass serialize deserialize test`() {
        val bean = GenericDataClass("String1", 25f)
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: GenericDataClass<String, Float> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("GenericBeanSerializerTest")
    fun `GenericBeanSerializer test`() {
        val bean = GenericBean<String, Float>()
        bean.somObject1 = "String"
        bean.somObject2 = 27f
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: GenericBean<String, Float> = SimpleXml.deserialize(xml)
        assertEquals(bean.somObject1, deserializedBean.somObject1)
        assertEquals(bean.somObject2, deserializedBean.somObject2)
    }

    @Test
    @JsName("GenericBeanSerializerTestObjectTest")
    fun `GenericBeanSerializer with object test`() {
        val bean = GenericBean<GenericBean<Double, Short>, Float>()
        val genericBean = GenericBean<Double, Short>()
        genericBean.somObject1 = 50.0
        genericBean.somObject2 = 9
        bean.somObject1 = genericBean
        bean.somObject2 = 27f
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: GenericBean<GenericBean<Double, Short>, Float> = SimpleXml.deserialize(xml)
        assertEquals(bean.somObject1?.somObject1, deserializedBean.somObject1?.somObject1)
        assertEquals(bean.somObject1?.somObject2, deserializedBean.somObject1?.somObject2)
        assertEquals(bean.somObject2, deserializedBean.somObject2)
    }

    @Test
    @JsName("GenericBeanWithOneParameterTest")
    fun `GenericBeanWithOneParameter test`() {
        val bean = GenericBeanWithOneParameter<String>()
        bean.somObject1 = "String"
        bean.somObject2 = "27f"
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: GenericBeanWithOneParameter<String> = SimpleXml.deserialize(xml)
        assertEquals(bean.somObject1, deserializedBean.somObject1)
        assertEquals(bean.somObject2, deserializedBean.somObject2)
    }

    @Test
    @JsName("GenericThreeBeanTest")
    fun `GenericThreeBean test`() {
        val bean = GenericThreeBean<GenericDataClass<Char, Double>, String, Int>()
        bean.somObject1 = GenericDataClass('$', 4.9)
        bean.somObject2 = "27f"
        bean.somObject3 = "27f"
        bean.somObject4 = 1
        bean.somObject5 = 2
        bean.somObject6 = 3
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: GenericThreeBean<GenericDataClass<Char, Double>, String, Int> = SimpleXml.deserialize(xml)
        assertEquals(bean.somObject1, deserializedBean.somObject1)
        assertEquals(bean.somObject2, deserializedBean.somObject2)
        assertEquals(bean.somObject3, deserializedBean.somObject3)
        assertEquals(bean.somObject4, deserializedBean.somObject4)
        assertEquals(bean.somObject5, deserializedBean.somObject5)
        assertEquals(bean.somObject6, deserializedBean.somObject6)
    }

//    @Test
//    fun `GenericExtension serialize deserialize test`() {
//        val bean = GenericExtension()
//        bean.somObject1 = "String1"
//        bean.somObject2 = 25f
//        val xml = SimpleXml.serialize(bean)
//        println(xml)
//        val deserializedBean: GenericExtension = SimpleXml.deserialize(xml)
//        assertEquals(bean, deserializedBean)
//    }

    @Test
    @JsName("OneDeepGenericDataTest")
    fun `OneDeepGenericData serialize deserialize test`() {
        val bean = OneDeepGenericData(GenericData("String"))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: OneDeepGenericData<String> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("OneDeepGenericClassTest")
    fun `OneDeepGenericClass serialize deserialize test`() {
        val bean = OneDeepGenericClass<String>()
        val genericClass = GenericClass<String>()
        genericClass.somObject1 = "String"
        bean.oneDeepObject1 = genericClass
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: OneDeepGenericClass<String> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("TwoDeepGenericDataTest")
    fun `TwoDeepGenericData serialize deserialize test`() {
        val bean = TwoDeepGenericData(OneDeepGenericData(GenericData("String")))
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: TwoDeepGenericData<String> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }

    @Test
    @JsName("TwoDeepGenericClassTest")
    fun `TwoDeepGenericClass serialize deserialize test`() {
        val bean = TwoDeepGenericClass<String>()
        val oneDeepGenericClass = OneDeepGenericClass<String>()
        val genericClass = GenericClass<String>()
        genericClass.somObject1 = "String"
        oneDeepGenericClass.oneDeepObject1 = genericClass
        bean.twoDeepObject1 = oneDeepGenericClass
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: TwoDeepGenericClass<String> = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)
    }
}