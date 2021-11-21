package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Converter
import ua.vald_zx.simplexml.ksp.Serializer
import ua.vald_zx.simplexml.ksp.xml.Tag
import ua.vald_zx.simplexml.ksp.xml.TagFather
import ua.vald_zx.simplexml.ksp.xml.model.XmlElement
import kotlin.reflect.KType

class ValueSerializer<T>(val converter: Converter<T>) : Serializer<T> {

    override val needArguments: Boolean = false

    override fun serialize(obj: T, genericTypeList: List<KType?>): String =
        converter.write(obj)

    override fun deserialize(raw: String, genericTypeList: List<KType?>): T =
        converter.read(raw)

    override fun buildXml(
        tagFather: TagFather,
        tagName: String,
        obj: T,
        genericTypeList: List<KType?>,
        attributeBlock: Tag.() -> Unit
    ) {
        tagFather.apply {
            tag(tagName, serialize(obj)) {
                attributeBlock()
            }
        }
    }

    override fun readData(element: XmlElement?, genericTypeList: List<KType?>): T {
        return deserialize(element?.text ?: error("Internal parse failure"))
    }
}