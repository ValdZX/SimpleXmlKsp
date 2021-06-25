import ua.vald_zx.simplexml.ksp.Serializer
import ua.vald_zx.simplexml.ksp.sample.beans.Auth
import ua.vald_zx.simplexml.ksp.xml.XmlReader.readXml
import ua.vald_zx.simplexml.ksp.xml.error.InvalidXml
import ua.vald_zx.simplexml.ksp.xml.tag


object AuthSerializer : Serializer<Auth> {
    override fun serialize(obj: Auth): String = tag("Auth") {
        tag("UserId", obj.userId)
        tag("Auth") {
            tag("Password", obj.password)
            tag("House") {
                tag("Device", obj.device) {
                    attr("time", obj.time)
                    attr("locale", obj.locale)
                }
            }
        }
    }.render()


    override fun deserialize(raw: String): Auth {
        val dom = raw.readXml() ?: throw InvalidXml()
        val layer0Tag0 = dom["UserId"]
        val layer0Tag1 = dom["Auth"]
        val layer1Tag2 = layer0Tag1["Password"]
        val layer1Tag3 = layer0Tag1["House"]
        val layer2Tag4 = layer1Tag3["Device"]
        val layer3Attribute5 = layer2Tag4.attribute("time")
        val layer3Attribute6 = layer2Tag4.attribute("locale")
        return Auth(
            userId = layer0Tag0.text,
            password = layer1Tag2.text,
            device = layer2Tag4.text,
            time = layer3Attribute5.text,
            locale = layer3Attribute6.text,
        )
    }
}