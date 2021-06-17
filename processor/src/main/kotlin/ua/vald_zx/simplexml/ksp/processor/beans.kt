package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ClassName

data class ClassToGenerate(
    val fullName: String,
    val name: String,
    val rootName: String,
    val packagePath: String,
    val properties: MutableList<Property> = mutableListOf()
)

data class Property(
    val propertyName: String,
    val unitName: String,
    val entry: String,
    val xmlType: XmlUnitType,
    val type: KSTypeReference,
    val path: String,
    val required: Boolean,
    val inline: Boolean,
)

data class FieldElement(
    val name: String,
    val propertyName: String = "",
    val type: XmlUnitType = XmlUnitType.TAG,
    val children: MutableList<FieldElement> = mutableListOf()
)

data class ToRegistration(
    val beanClass: ClassName,
    val serializerClass: ClassName,
)

enum class XmlUnitType {
    TAG,
    LIST,
    ATTRIBUTE,
    UNKNOWN
}