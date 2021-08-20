package serializers

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
}