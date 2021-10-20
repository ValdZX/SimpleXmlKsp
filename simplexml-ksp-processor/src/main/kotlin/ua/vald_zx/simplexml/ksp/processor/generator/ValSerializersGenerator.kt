package ua.vald_zx.simplexml.ksp.processor.generator

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.Field

data class ValSerializer(
    val serializerVariableName: String,
    val genericTypesVariableName: String? = null,
)

data class FieldSerializer(
    val firstValSerializer: ValSerializer,
    val secondValSerializer: ValSerializer? = null
)

fun FunSpec.Builder.generateAndGetSerializers(classToGenerate: ClassToGenerate): Map<Field, FieldSerializer> {
    val propertyToSerializerName = mutableMapOf<Field, FieldSerializer>()
    val fields = classToGenerate.fields
    val genericFields = mutableListOf<Field>()
    val typeParameters = classToGenerate.typeParameters.toSet()
    if (typeParameters.isNotEmpty()) {
        val typeMap = mutableMapOf<String, ValSerializer>()
        fields
            .filter { hasGenericArg(it, typeParameters) }
            .forEach { field ->
                genericFields.add(field)
                val (firstType, secondType) = when (field) {
                    is Field.List -> field.entryType to null
                    is Field.Map -> field.keyType to field.entryType
                    else -> field.fieldType to null
                }
                if (firstType != null) {
                    genericValSerializer(
                        field,
                        typeMap,
                        propertyToSerializerName,
                        typeParameters,
                        firstType,
                        secondType
                    )
                }

            }
    }
    val objectProperties = (fields - genericFields)
        .filter { property -> property.converterType == null }
    val propertyToTypeSerializer = objectProperties
        .associateWith { property ->
            when (property) {
                is Field.List -> {
                    listOf(property.entryType.toString())
                }
                is Field.Map -> {
                    val keyClassName = property.keyType.toString()
                    val valueClassName = property.entryType.toString()
                    listOf(keyClassName, valueClassName)
                }
                else -> {
                    listOf(property.fieldType.toString())
                }
            }
        }
    propertyToSerializerName.putAll(propertyToTypeSerializer.mapValues { (_, classes) ->
        val className = classes[0]
        val valueDeclaration = classes.getOrNull(1)
        FieldSerializer(
            firstValSerializer = ValSerializer(className.toSerializerValName()),
            secondValSerializer = valueDeclaration?.toSerializerValName()?.let { ValSerializer(it) }
        )
    })
    propertyToTypeSerializer.values.flatten().toSet().forEach { className ->
        renderSerializerVal(className)
    }

    val propertiesWithConverter = (fields - objectProperties - genericFields)
        .filter { property -> property.converterType != null }
    val propertyToConverterSerializer = propertiesWithConverter
        .associateWith { property ->
            val converterType = property.converterType ?: error("Unknown error")
            converterType.declaration.simpleName.asString()
        }
    propertyToSerializerName.putAll(propertyToConverterSerializer.mapValues { (_, className) ->
        FieldSerializer(ValSerializer(className.toSerializerValName()))
    })
    propertyToConverterSerializer.values.toSet().forEach { className ->
        addStatement("val ${className.toSerializerValName()} = ValueSerializer(${className}())")
    }
    return propertyToSerializerName
}

private fun String.toSerializerValName(): String {
    return replaceFirstChar { it.lowercase() } + "Serializer"
}

private fun hasGenericArg(field: Field, typeParameters: Set<KSTypeParameter>): Boolean {
    return when (field) {
        is Field.Tag -> typeParameters.containsInTreeIndexOf(field.fieldType?.resolve()).contains
        is Field.Attribute -> typeParameters.containsInTreeIndexOf(field.fieldType.resolve()).contains
        is Field.Text -> typeParameters.containsInTreeIndexOf(field.fieldType?.resolve()).contains
        is Field.List -> typeParameters.containsInTreeIndexOf(field.entryType.resolve()).contains
        is Field.Map -> {
            typeParameters.containsInTreeIndexOf(field.keyType.resolve()).contains
                    || typeParameters.containsInTreeIndexOf(field.entryType.resolve()).contains
        }
        is Field.IsTag -> false
    }
}

data class TypeContainsResult(
    val index: Int,
    val isFirstLayer: Boolean
) {
    val contains: Boolean
        get() = index >= 0
}

private fun Collection<KSTypeParameter>.containsInTreeIndexOf(fieldType: KSType?, layer: Int = 0): TypeContainsResult {
    if (fieldType == null) return TypeContainsResult(-1, layer == 0)
    val index = map { it.toString() }.indexOf(fieldType.toString().replace("?", ""))
    return if (index == -1) {
        val arguments = fieldType.arguments
        if (arguments.isNotEmpty()) {
            arguments.forEach { ksTypeParameter ->
                return containsInTreeIndexOf(ksTypeParameter.type?.resolve(), layer + 1)
            }
        }
        TypeContainsResult(-1, layer == 0)
    } else {
        TypeContainsResult(index, layer == 0)
    }
}

private fun FunSpec.Builder.genericValSerializer(
    field: Field,
    typeMap: MutableMap<String, ValSerializer>,
    propertyToSerializerName: MutableMap<Field, FieldSerializer>,
    typeParameters: Set<KSTypeParameter>,
    firstType: KSTypeReference,
    secondType: KSTypeReference? = null,
) {
    val firstTypeName = firstType.toString()
    val secondTypeName = secondType?.toString()
    val firstCachedFieldSerializer = typeMap[firstTypeName]
    val secondCachedFieldSerializer = typeMap[secondTypeName]
    val firstIndexResult = typeParameters.containsInTreeIndexOf(firstType.resolve())
    val secondIndexResult = typeParameters.containsInTreeIndexOf(secondType?.resolve())

    val firstSerializer: ValSerializer
    var secondSerializer: ValSerializer? = null
    if (firstCachedFieldSerializer != null) {
        firstSerializer = firstCachedFieldSerializer
    } else {
        typeMap[firstTypeName] =
            if (firstIndexResult.contains) {
                if (firstIndexResult.isFirstLayer) {
                    renderGenericSerializerVal(firstTypeName, firstIndexResult.index).apply { firstSerializer = this }
                } else {
                    renderSerializerValWithGenericField(firstTypeName).apply { firstSerializer = this }
                }
            } else {
                renderSerializerVal(firstTypeName).apply { firstSerializer = this }
            }
    }
    if (secondTypeName != null) {
        if (secondCachedFieldSerializer != null) {
            secondSerializer = secondCachedFieldSerializer
        } else {
            typeMap[secondTypeName] = if (secondIndexResult.contains) {
                if (secondIndexResult.isFirstLayer) {
                    renderGenericSerializerVal(secondTypeName, secondIndexResult.index).apply {
                        secondSerializer = this
                    }
                } else {
                    renderSerializerValWithGenericField(secondTypeName).apply { secondSerializer = this }
                }
            } else {
                renderSerializerVal(secondTypeName).apply { secondSerializer = this }
            }
        }
    }
    propertyToSerializerName[field] = FieldSerializer(firstSerializer, secondSerializer)
}

private fun FunSpec.Builder.renderSerializerVal(className: String): ValSerializer {
    val valueName = className.toSerializerValName()
    addStatement("val $valueName = GlobalSerializersLibrary.findSerializers(${className}::class)")
    return ValSerializer(valueName)
}

private fun FunSpec.Builder.renderSerializerValWithGenericField(className: String): ValSerializer {
    val valueName = className.toSerializerValName()
    addStatement("val $valueName = GlobalSerializersLibrary.findSerializers(${className}::class)")
    return ValSerializer(valueName, "genericTypeList")
}

private fun FunSpec.Builder.renderGenericSerializerVal(className: String, index: Int): ValSerializer {
    val classNameLowCase = className.replaceFirstChar { it.lowercase() }
    val classTypeArguments = "${classNameLowCase}Args"
    val valueName = "${classNameLowCase}Serializer"
    addStatement("val ${classNameLowCase}Type = genericTypeList[$index].type")
    addStatement("val $classTypeArguments = ${classNameLowCase}Type?.arguments.orEmpty()")
    addStatement("val $valueName = GlobalSerializersLibrary.findSerializers(${classNameLowCase}Type?.classifier as KClass<Any>)")
    return ValSerializer(valueName, classTypeArguments)
}
