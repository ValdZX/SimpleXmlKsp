import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.sample.Auth
import ua.vald_zx.simplexml.ksp.sample.AuthSerializer
import ua.vald_zx.simplexml.ksp.sample.ModuleInitializer
import kotlin.test.Test
import kotlin.test.assertIs

class GenerationTest {
    @Test
    fun findSerializersTest() {
        ModuleInitializer.init()
        assertIs<AuthSerializer>(GlobalSerializersLibrary.findSerializers(Auth::class))
    }

    @Test
    fun serializeTest() {
        ModuleInitializer.init()
        println(SimpleXml.serialize(Auth()))
    }

    @Test
    fun deserializeTest() {
        ModuleInitializer.init()
        assertIs<Auth>(SimpleXml.deserialize<Auth>("<Auth/>"))
    }
}