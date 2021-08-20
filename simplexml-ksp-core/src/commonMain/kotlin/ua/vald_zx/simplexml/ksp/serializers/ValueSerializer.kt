package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Converter
import ua.vald_zx.simplexml.ksp.Serializer
import ua.vald_zx.simplexml.ksp.xml.Tag
import ua.vald_zx.simplexml.ksp.xml.TagFather
import ua.vald_zx.simplexml.ksp.xml.model.XmlElement
import kotlin.reflect.KTypeProjection

class ValueSerializer<T>(val converter: Converter<T>) : Serializer<T> {

    override fun serialize(obj: T, genericTypeList: List<KTypeProjection>): String =
        converter.write(obj)

    override fun deserialize(raw: String, genericTypeList: List<KTypeProjection>): T =
        converter.read(raw)

    override fun buildXml(
        tagFather: TagFather,
        tagName: String,
        obj: T,
        genericTypeList: List<KTypeProjection>,
        attributeBlock: Tag.() -> Unit
    ) {
        tagFather.apply {
            tag(tagName, serialize(obj)) {
                attributeBlock()
            }
        }
    }

    override fun readData(element: XmlElement?, genericTypeList: List<KTypeProjection>): T {
        return deserialize(element?.text ?: error("Internal parse failure"))
    }
}