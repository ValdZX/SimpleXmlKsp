package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.ClassName

data class ClassToGenerate(
    val bean: KSClassDeclaration,
    val fullName: String,
    val name: String,
    val rootName: String,
    val packagePath: String,
    val propertyElements: MutableList<PropertyElement> = mutableListOf()
)

data class PropertyElement(
    val propertyName: String,
    val propertyType: KSTypeReference,
    val xmlName: String,
    val xmlType: XmlUnitType,
    val xmlPath: String,
    val required: Boolean,
    val requiredToConstructor: Boolean,
    val listEntryName: String,
    val inlineList: Boolean
)

data class DomElement(
    val property: PropertyElement? = null,
    val xmlName: String = property?.xmlName.orEmpty(),
    val propertyName: String = property?.propertyName.orEmpty(),
    val type: XmlUnitType = property?.xmlType ?: XmlUnitType.TAG,
    val children: MutableList<DomElement> = mutableListOf()
) {
    val entryName: String
        get() = property?.listEntryName.orEmpty()
    val inlineList: Boolean
        get() = property?.inlineList ?: false
    val isNullable: Boolean
        get() = property?.let { it.propertyType.resolve().nullability != Nullability.NOT_NULL }
            ?: false
}

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