package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Converter
import ua.vald_zx.simplexml.ksp.Serializer
import ua.vald_zx.simplexml.ksp.xml.Tag
import ua.vald_zx.simplexml.ksp.xml.TagFather
import ua.vald_zx.simplexml.ksp.xml.model.XmlElement

class ValueSerializer<T>(val converter: Converter<T>) : Serializer<T> {

    override fun serialize(obj: T): String = converter.write(obj)

    override fun deserialize(raw: String): T = converter.read(raw)

    override fun buildXml(tagFather: TagFather, tagName: String, obj: T, attributeBlock: Tag.() -> Unit) {
        tagFather.apply {
            tag(tagName, serialize(obj)) {
                attributeBlock()
            }
        }
    }

    override fun readData(element: XmlElement?): T {
        return deserialize(element?.text ?: error("Internal parse failure"))
    }
}