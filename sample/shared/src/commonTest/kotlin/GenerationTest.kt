import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.sample.Auth
import ua.vald_zx.simplexml.ksp.sample.AuthSerializer
import ua.vald_zx.simplexml.sample.custompackage.SampleModuleInitializer
import kotlin.test.Test
import kotlin.test.assertIs

class GenerationTest {
    @Test
    fun findSerializersTest() {
        SampleModuleInitializer.init()
        assertIs<AuthSerializer>(GlobalSerializersLibrary.findSerializers(Auth::class))
    }

    @Test
    fun serializeTest() {
        SampleModuleInitializer.init()
        println(SimpleXml.serialize(Auth()))
    }

    @Test
    fun deserializeTest() {
        SampleModuleInitializer.init()
        assertIs<Auth>(SimpleXml.deserialize<Auth>("<Auth/>"))
    }
}