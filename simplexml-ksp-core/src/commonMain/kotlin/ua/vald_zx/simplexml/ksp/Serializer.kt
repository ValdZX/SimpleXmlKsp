package ua.vald_zx.simplexml.ksp

import ua.vald_zx.simplexml.ksp.xml.Tag
import ua.vald_zx.simplexml.ksp.xml.TagFather
import ua.vald_zx.simplexml.ksp.xml.model.XmlElement
import kotlin.reflect.KType

interface Serializer<T> {

    val needArguments: Boolean

    fun serialize(obj: T, genericTypeList: List<KType?> = emptyList()): String

    fun deserialize(raw: String, genericTypeList: List<KType?> = emptyList()): T

    fun buildXml(
        tagFather: TagFather,
        tagName: String,
        obj: T,
        genericTypeList: List<KType?> = emptyList(),
        attributeBlock: Tag.() -> Unit = {}
    )

    fun readData(element: XmlElement?, genericTypeList: List<KType?> = emptyList()): T
}