package ua.vald_zx.simplexml.ksp.serializers

import ua.vald_zx.simplexml.ksp.Serializer
import ua.vald_zx.simplexml.ksp.xml.Tag
import ua.vald_zx.simplexml.ksp.xml.TagFather
import ua.vald_zx.simplexml.ksp.xml.XmlReader.readXml
import ua.vald_zx.simplexml.ksp.xml.tag

abstract class ObjectSerializer<T> : Serializer<T> {

    protected abstract val rootName: String

    public override fun serialize(obj: T): String {
        return tag(rootName) { buildXml(this, obj) }.render()
    }

    override fun buildXml(tagFather: TagFather, tagName: String, obj: T, attributeBlock: Tag.() -> Unit) {
        tagFather.apply {
            tag(tagName) {
                attributeBlock()
                buildXml(this, obj)
            }
        }
    }

    override fun deserialize(raw: String): T {
        val dom = raw.readXml() ?: error("Read xml failed")
        return readData(dom)
    }

    abstract fun buildXml(tagFather: TagFather, obj: T)
}