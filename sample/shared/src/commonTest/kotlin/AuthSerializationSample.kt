import ua.vald_zx.simplexml.ksp.Serializer
import ua.vald_zx.simplexml.ksp.sample.beans.Auth
import ua.vald_zx.simplexml.ksp.xml.XmlReader.readXml
import ua.vald_zx.simplexml.ksp.xml.tag


public object AuthSerializer : Serializer<Auth> {
    public override fun serialize(obj: Auth): String = tag("Auth") {
        obj.legend?.let { tag("legend", it) }
        obj.legend2?.let { tag("legend2", it) }
        val legend3 = obj.legend3
        if (legend3 != null) {
            tag("legend3", legend3) {
                obj.legend4?.let { attr("legend4", it) }
            }
        } else {
            tag("legend3") {
                obj.legend4?.let { attr("legend4", it) }
            }
        }
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
        tag("Phones") {
            obj.phones.forEach {
                tag("Phone", it)
            }
        }
        obj.phones.forEach {
            tag("Address", it)
        }
    }.render()


    public override fun deserialize(raw: String): Auth {
        val dom = raw.readXml()
        val layer0Tag0 = dom?.get("legend")
        val layer0Tag1 = dom?.get("legend2")
        val layer0Tag2 = dom?.get("legend3")
        val layer1Attribute3 = layer0Tag2?.attribute("legend4")
        val layer0Tag4 = dom?.get("UserId")
        val layer0Tag5 = dom?.get("location")
        val layer0Tag6 = dom?.get("Auth")
        val layer0Tag10 = dom?.get("Phones")
        val layer0List11 = layer0Tag10?.getAll("Phone")
        val layer0List12 = dom?.getAll("Address")
        val layer1Tag7 = layer0Tag6?.get("Password")
        val layer1Tag8 = layer0Tag6?.get("House")
        val layer2Tag9 = layer1Tag8?.get("Device")
        val layer3Attribute10 = layer2Tag9?.attribute("time")
        val layer3Attribute11 = layer2Tag9?.attribute("locale")
        return Auth(
            userId = layer0Tag4?.text ?: error("""fields userId value is required"""),
            password = layer1Tag7?.text ?: error("""fields password value is required"""),
        ).apply {
            if (layer0Tag0 != null) legend = layer0Tag0.text
            legend2 = layer0Tag1?.text ?: error("""fields legend2 value is required""")
            if (layer0Tag2 != null) legend3 = layer0Tag2.text
            location = layer0Tag5?.text ?: error("""fields location value is required""")
            device = layer2Tag9?.text ?: error("""fields device value is required""")
            if (layer1Attribute3 != null) legend4 = layer1Attribute3.text
            time = layer3Attribute10?.text ?: error("""fields time value is required""")
            locale = layer3Attribute11?.text ?: error("""fields locale value is required""")
            phones = layer0List11?.map { it.text } ?: emptyList()
            addresses = layer0List12?.map { it.text } ?: emptyList()
        }
    }
}