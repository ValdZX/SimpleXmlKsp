import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import ua.vald_zx.simplexml.ksp.Serializer
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.sample.Auth
import ua.vald_zx.simplexml.ksp.sample.AuthSerializer
import ua.vald_zx.simplexml.ksp.sample.custompackage.SampleModuleInitializer
import ua.vald_zx.simplexml.ksp.xml.XmlReader.readXml
import ua.vald_zx.simplexml.ksp.xml.error.InvalidXml
import ua.vald_zx.simplexml.ksp.xml.tag
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
        val value = SimpleXml.deserialize<Auth>("<Auth><UserId>Vald_ZX</UserId><Auth><Password>Linkoln</Password><House><Device>Android</Device></House></Auth></Auth>")
        assertIs<Auth>(value)
    }
}

object AuthSer : Serializer<Auth> {

    override fun serialize(obj: Auth): String {
        return tag("Auth") {
            tag("UserId", obj.userId)
            tag("Auth") {
                tag("Password", obj.password)
                tag("Device", obj.device)
            }
        }.render()
    }

    override fun deserialize(raw: String): Auth {
        val dom = raw.readXml() ?: throw InvalidXml()
        val layer1Tag1 = dom["Auth"]
        val layer2Tag1 = layer1Tag1["House"]
        return Auth(
            userId = dom["UserId"].text,
            password = layer1Tag1["Password"].text,
            device = layer2Tag1["Device"].text
        )
    }
}