package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ClassName

data class ClassToGenerate(
    val bean: KSClassDeclaration,
    val fullName: String,
    val name: String,
    val rootName: String,
    val packagePath: String,
    val properties: MutableList<Property> = mutableListOf()
)

data class Property(
    val name: String,
    val xmlName: String,
    val entry: String,
    val xmlType: XmlUnitType,
    val type: KSTypeReference,
    val path: String,
    val required: Boolean,
    val hasDefault: Boolean,
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