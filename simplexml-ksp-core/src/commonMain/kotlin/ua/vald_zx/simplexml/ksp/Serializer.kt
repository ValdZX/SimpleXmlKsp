package ua.vald_zx.simplexml.ksp

import ua.vald_zx.simplexml.ksp.xml.Tag
import ua.vald_zx.simplexml.ksp.xml.TagFather
import ua.vald_zx.simplexml.ksp.xml.model.XmlElement
import kotlin.reflect.KTypeProjection

interface Serializer<T> {

    fun serialize(obj: T, genericTypeList: List<KTypeProjection> = emptyList()): String

    fun deserialize(raw: String, genericTypeList: List<KTypeProjection> = emptyList()): T

    fun buildXml(
        tagFather: TagFather,
        tagName: String,
        obj: T,
        genericTypeList: List<KTypeProjection> = emptyList(),
        attributeBlock: Tag.() -> Unit = {}
    )

    fun readData(element: XmlElement?, genericTypeList: List<KTypeProjection> = emptyList()): T
}