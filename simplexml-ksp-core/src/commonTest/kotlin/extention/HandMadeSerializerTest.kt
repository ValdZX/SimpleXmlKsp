package extention

import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import ua.vald_zx.simplexml.ksp.SimpleXml
import kotlin.test.Test
import kotlin.test.assertEquals

class HandMadeSerializerTest {
    @Test
    fun `ExtBeanSerializer test`() {
        GlobalSerializersLibrary.add(ExtBean::class) {
            ExtBeanSerializer
        }
        val bean = ExtBean<String, Float, Char>()
        bean.somObject1 = "String"
        bean.somObject2 = 27f
        bean.somObject3 = 3f
        bean.somObject4 = '~'
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: ExtBean<String, Float, Char> = SimpleXml.deserialize(xml)
        assertEquals(bean.somObject1, deserializedBean.somObject1)
        assertEquals(bean.somObject2, deserializedBean.somObject2)
        assertEquals(bean.somObject3, deserializedBean.somObject3)
        assertEquals(bean.somObject4, deserializedBean.somObject4)
    }
}