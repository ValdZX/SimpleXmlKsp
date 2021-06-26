import ua.vald_zx.simplexml.ksp.Serializer
import ua.vald_zx.simplexml.ksp.sample.beans.Auth
import ua.vald_zx.simplexml.ksp.xml.XmlReader.readXml
import ua.vald_zx.simplexml.ksp.xml.tag


public object AuthSerializer : Serializer<Auth> {
    public override fun serialize(obj: Auth): String = tag("Auth") {
        tag("UserId", obj.userId)
        tag("location", obj.location)
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


    public override fun deserialize(raw: String): Auth {
        val dom = raw.readXml()
        val layer0Tag0 = dom?.get("UserId")
        val layer0Tag1 = dom?.get("location")
        val layer0Tag2 = dom?.get("Auth")
        val layer1Tag3 = layer0Tag2?.get("Password")
        val layer1Tag4 = layer0Tag2?.get("House")
        val layer2Tag5 = layer1Tag4?.get("Device")
        val layer3Attribute6 = layer2Tag5?.attribute("time")
        val layer3Attribute7 = layer2Tag5?.attribute("locale")
        return Auth(
            userId = layer0Tag0?.text ?: error(""),
            password = layer1Tag3?.text ?: error(""),
        ).apply {
            if (layer0Tag1 != null) location = layer0Tag1.text
//            device = layer2Tag5?.text
//            time = layer3Attribute6?.text
//            locale = layer3Attribute7?.text
        }

    }
}