import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.sample.Auth
import ua.vald_zx.simplexml.ksp.sample.AuthSerializer
import ua.vald_zx.simplexml.ksp.sample.custompackage.SampleModuleInitializer
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
        val authToSerialize = Auth(userId = "Vald_ZX", password = "Linkoln", device = "Android")
        val message = SimpleXml.serialize(authToSerialize)
        println(message)
    }

    @Test
    fun deserializeTest() {
        SampleModuleInitializer.init()
        val value = SimpleXml.deserialize<Auth>(
            "<Auth><UserId>Vald_ZX</UserId><Auth><Password>Linkoln</Password><House><Device>Android</Device></House></Auth></Auth>"
        )
        assertIs<Auth>(value)
    }
}