package serializers

import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import ua.vald_zx.simplexml.ksp.SimpleXml
import kotlin.test.Test
import kotlin.test.assertEquals

class HandMadeSerializerTest {
    @Test
    fun `GenericBeanSerializer test`() {
        GlobalSerializersLibrary.add(GenericBean::class) {
            GenericBeanSerializer
        }
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
        GlobalSerializersLibrary.add(GenericBean::class) {
            GenericBeanSerializer
        }
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
}