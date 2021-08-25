package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName

data class ClassToGenerate(
    val bean: KSClassDeclaration,
    val typeParameters: List<KSTypeParameter>,
    val fullName: String,
    val name: String,
    val rootName: String,
    val packagePath: String,
    val propertyElements: MutableList<PropertyElement>
) {
    val dom: List<DomElement> by lazy { makeDom(this) }
}

data class PropertyElement(
    val propertyName: String,
    val propertyType: KSTypeReference,
    val xmlName: String,
    val xmlType: XmlUnitType,
    val xmlPath: String,
    val required: Boolean,
    val isVariable: Boolean,
    val isMutableCollection: Boolean,
    val isConstructorParameter: Boolean,
    val hasDefaultValue: Boolean,
    val listEntryName: String,
    val keyMapName: String,
    val valueMapName: String,
    val inlineList: Boolean,
    val converterType: KSType?,
    val propertyEntryType: KSTypeReference? = null,
    val propertyKeyType: KSTypeReference? = null,
    val propertyValueType: KSTypeReference? = null
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
    val keyName: String
        get() = property?.keyMapName.orEmpty()
    val valueName: String
        get() = property?.valueMapName.orEmpty()
    val inlineList: Boolean
        get() = property?.inlineList ?: false
    val isNullable: Boolean
        get() = property?.let { it.propertyType.resolve().nullability != Nullability.NOT_NULL }
            ?: false
}

data class GeneratedSerializerSpec(
    val beanClass: ClassName,
    val serializerClass: ClassName,
    val packageName: String,
)

enum class XmlUnitType {
    TAG,
    LIST,
    MAP,
    ATTRIBUTE,
    UNKNOWN
}