package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ClassName
import ua.vald_zx.simplexml.ksp.processor.generator.element.deserialization.*
import ua.vald_zx.simplexml.ksp.processor.generator.element.serialization.*

data class ClassToGenerate(
    val bean: KSClassDeclaration,
    val typeParameters: List<KSTypeParameter>,
    val fullName: String,
    val name: String,
    val rootName: String,
    val packagePath: String,
    val fields: MutableList<Field>
) {
    val dom: List<Field> by lazy { makeDom(this) }
}

sealed interface Field {
    val required: Boolean
    val hasDefaultValue: Boolean
    val isNullable: Boolean
    val isMutable: Boolean
    val isConstructorParameter: Boolean
    val path: String
    val fieldName: String
    val fieldType: KSTypeReference?
    val converterType: KSType?
    val serializationGenerator: ElementSerializationGenerator
    val deserializationGenerator: ElementDeserializationGenerator

    interface IsTag : Field {
        val tagName: String
        val children: MutableList<Field>
    }

    data class Tag(
        override val tagName: String,
        override val required: Boolean = false,
        override val isNullable: Boolean = false,
        override val isMutable: Boolean = false,
        override val isConstructorParameter: Boolean = false,
        override val path: String = "",
        override val fieldName: String = "",
        override val converterType: KSType? = null,
        override val hasDefaultValue: Boolean = false,
        override val fieldType: KSTypeReference? = null,
        override val children: MutableList<Field> = mutableListOf()
    ) : IsTag {
        override val serializationGenerator: ElementSerializationGenerator = TagSerializationGenerator(this)
        override val deserializationGenerator: ElementDeserializationGenerator = TagDeserializationGenerator(this)
    }

    data class Attribute(
        override val required: Boolean,
        override val hasDefaultValue: Boolean,
        override val isNullable: Boolean,
        override val isMutable: Boolean,
        override val isConstructorParameter: Boolean,
        override val path: String,
        override val fieldName: String,
        override val converterType: KSType?,
        override val fieldType: KSTypeReference,
        val attributeName: String
    ) : Field {
        override val serializationGenerator: ElementSerializationGenerator = AttributeSerializationGenerator(this)
        override val deserializationGenerator: ElementDeserializationGenerator = AttributeDeserializationGenerator(this)
    }

    data class Text(
        override val required: Boolean,
        override val hasDefaultValue: Boolean,
        override val isNullable: Boolean,
        override val isMutable: Boolean,
        override val isConstructorParameter: Boolean,
        override val converterType: KSType?,
        override val fieldName: String,
        override val fieldType: KSTypeReference? = null,
    ) : Field {
        override val path: String = ""
        override val serializationGenerator: ElementSerializationGenerator = TextSerializationGenerator(this)
        override val deserializationGenerator: ElementDeserializationGenerator = TextDeserializationGenerator(this)
    }

    data class List(
        override val required: Boolean,
        override val hasDefaultValue: Boolean,
        override val isNullable: Boolean,
        override val isMutable: Boolean,
        override val isConstructorParameter: Boolean,
        override val path: String,
        override val tagName: String,
        override val fieldName: String,
        override val fieldType: KSTypeReference,
        override val children: MutableList<Field> = mutableListOf(),
        override val converterType: KSType?,
        val isInline: Boolean,
        val isMutableCollection: Boolean,
        val entryName: String,
        val attributes: MutableList<Attribute> = mutableListOf(),
        val entryType: KSTypeReference,
    ) : IsTag {
        override val serializationGenerator: ElementSerializationGenerator = ListSerializationGenerator(this)
        override val deserializationGenerator: ElementDeserializationGenerator = ListDeserializationGenerator(this)
    }

    data class Map(
        override val required: Boolean,
        override val hasDefaultValue: Boolean,
        override val isNullable: Boolean,
        override val isMutable: Boolean,
        override val isConstructorParameter: Boolean,
        override val tagName: String,
        override val path: String,
        override val fieldName: String,
        override val fieldType: KSTypeReference,
        override val children: MutableList<Field> = mutableListOf(),
        override val converterType: KSType?,
        val isInline: Boolean,
        val isAttribute: Boolean,
        val isMutableCollection: Boolean,
        val attributes: MutableList<Attribute> = mutableListOf(),
        val keyName: String,
        val keyType: KSTypeReference,
        val entryName: String,
        val entryType: KSTypeReference
    ) : IsTag {
        override val serializationGenerator: ElementSerializationGenerator = MapSerializationGenerator(this)
        override val deserializationGenerator: ElementDeserializationGenerator = MapDeserializationGenerator(this)
    }
}

data class GeneratedSerializerSpec(
    val beanClass: ClassName,
    val serializerClass: ClassName,
    val packageName: String,
)