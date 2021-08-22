package ua.vald_zx.simplexml.ksp.test.generics

import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.sample.custompackage.SampleModuleInitializer
import kotlin.test.BeforeTest
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

    @Test
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
}