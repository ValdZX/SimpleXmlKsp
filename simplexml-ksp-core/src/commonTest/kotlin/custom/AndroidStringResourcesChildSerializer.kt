package custom

import ua.vald_zx.simplexml.ksp.GlobalSerializersLibrary
import ua.vald_zx.simplexml.ksp.exception.DeserializeException
import ua.vald_zx.simplexml.ksp.serializers.ObjectSerializer
import ua.vald_zx.simplexml.ksp.xml.TagFather
import ua.vald_zx.simplexml.ksp.xml.model.TagXmlElement
import ua.vald_zx.simplexml.ksp.xml.model.XmlElement
import kotlin.String
import kotlin.reflect.KType

object AndroidStringResourcesChildSerializer :
    ObjectSerializer<AndroidStringResourcesChild>() {
    public override val rootName: String = "AndroidStringResourcesChild"

    override fun buildXml(
        tagFather: TagFather,
        obj: AndroidStringResourcesChild,
        genericTypeList: List<KType?>
    ) {
        val stringSerializer = GlobalSerializersLibrary.findSerializers(String::class)
        tagFather.apply {
            tag("resources") {
                attr("xmlns:android", stringSerializer.serialize(obj.ns))
                obj.resources.mapNotNull { (key, value) ->
                    key.let { value.let { key to value } }
                }.forEach { (key, value) ->
                    stringSerializer.buildXml(this, "string", value) {
                        attr("name", stringSerializer.serialize(key))
                    }
                }
            }
        }
    }

    @Suppress("SENSELESS_COMPARISON", "USELESS_ELVIS")
    override fun readData(element: XmlElement?, genericTypeList: List<KType?>):
            AndroidStringResourcesChild {
        val stringSerializer = GlobalSerializersLibrary.findSerializers(String::class)
        element as TagXmlElement?
        val layer0Tag0 = element?.get("resources")
        val layer0Map1 = layer0Tag0?.getAll("string")
        val layer1Attribute2 = layer0Tag0?.attribute("xmlns:android")
        return AndroidStringResourcesChild(
            resources = layer0Map1?.associate {
                val keyAttribute = it.attribute("name")
                val keyData = stringSerializer.readData(keyAttribute)
                val valueData = stringSerializer.readData(it)
                keyData to valueData
            }
                ?: throw DeserializeException("""resources field resources value is required"""),
        ).apply {
            ns = stringSerializer.readData(
                layer1Attribute2 ?: throw DeserializeException("""field ns value is required""")
            )
        }
    }
}
