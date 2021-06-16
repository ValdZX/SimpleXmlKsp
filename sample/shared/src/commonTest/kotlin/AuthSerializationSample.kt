import ua.vald_zx.simplexml.ksp.Serializer
import ua.vald_zx.simplexml.ksp.sample.beans.Auth
import ua.vald_zx.simplexml.ksp.xml.XmlReader.readXml
import ua.vald_zx.simplexml.ksp.xml.error.InvalidXml
import ua.vald_zx.simplexml.ksp.xml.tag


object AuthSerializationSample : Serializer<Auth> {

    override fun serialize(obj: Auth): String {
        return tag("Auth") {
            tag("UserId", obj.userId)
            tag("Auth") {
                tag("Password", obj.password)
                tag("Device", obj.device) {
                    attr("time", obj.time)
                }
            }
        }.render()
    }

    override fun deserialize(raw: String): Auth {
        val dom = raw.readXml() ?: throw InvalidXml()
        val layer1Tag1 = dom["Auth"]
        val layer2Tag1 = layer1Tag1["House"]
        val layer3Tag1 = layer2Tag1["Device"]
        val layer3Attribute1 = layer3Tag1.attribute("time")
        val layer3Attribute2 = layer3Tag1.attribute("locale")
        return Auth(
            userId = dom["UserId"].text,
            password = layer1Tag1["Password"].text,
            device = layer3Tag1.text,
            time = layer3Attribute1.text,
            locale = layer3Attribute2.text
        )
    }
}