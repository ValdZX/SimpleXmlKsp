package custom

import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import ua.vald_zx.simplexml.ksp.SimpleXml
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomSerializerTest {
    @Test
    fun androidStringResourcesChildSerializerTest() {
        GlobalSerializersLibrary.add(AndroidStringResourcesChild::class) {
            AndroidStringResourcesChildSerializer
        }
        SimpleXml.pretty = true
        val bean = AndroidStringResourcesChild(
            resources = mapOf(
                "appName" to "The best app",
                "greetings" to "Hello!"
            )
        )
        val xml = SimpleXml.serialize(bean)
        println(xml)
        val deserializedBean: AndroidStringResourcesChild = SimpleXml.deserialize(xml)
        assertEquals(bean, deserializedBean)

    }
}