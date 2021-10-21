package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Serializer
import ua.vald_zx.simplexml.ksp.SimpleXml
import ua.vald_zx.simplexml.ksp.xml.Tag
import ua.vald_zx.simplexml.ksp.xml.TagFather
import ua.vald_zx.simplexml.ksp.xml.XmlReader.readXml
import ua.vald_zx.simplexml.ksp.xml.tag
import kotlin.reflect.KType

abstract class ObjectSerializer<T> : Serializer<T> {

    protected abstract val rootName: String

    override fun serialize(obj: T, genericTypeList: List<KType?>): String {
        return tag(rootName, pretty = SimpleXml.pretty) { buildXml(this, obj, genericTypeList) }.render()
    }

    override fun buildXml(
        tagFather: TagFather,
        tagName: String,
        obj: T,
        genericTypeList: List<KType?>,
        attributeBlock: Tag.() -> Unit
    ) {
        tagFather.apply {
            tag(tagName) {
                attributeBlock()
                buildXml(this, obj, genericTypeList)
            }
        }
    }

    override fun deserialize(raw: String, genericTypeList: List<KType?>): T {
        val dom = raw.readXml() ?: error("Read xml failed")
        return readData(dom, genericTypeList)
    }

    abstract fun buildXml(tagFather: TagFather, obj: T, genericTypeList: List<KType?>)
}