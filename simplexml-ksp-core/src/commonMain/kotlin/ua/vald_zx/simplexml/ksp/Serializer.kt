package ua.vald_zx.simplexml.ksp

import ua.vald_zx.simplexml.ksp.xml.Tag
import ua.vald_zx.simplexml.ksp.xml.TagFather
import ua.vald_zx.simplexml.ksp.xml.model.XmlElement

interface Serializer<T> {

    fun serialize(obj: T): String

    fun deserialize(raw: String): T

    fun buildXml(tagFather: TagFather, tagName: String, obj: T, attributeBlock: Tag.() -> Unit = {})

    fun readData(element: XmlElement?): T
}