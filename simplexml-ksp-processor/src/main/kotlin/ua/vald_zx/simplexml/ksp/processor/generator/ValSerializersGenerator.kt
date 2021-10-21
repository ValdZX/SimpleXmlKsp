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
        val typeSerializersMap = mutableMapOf<String, ValSerializer>()
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
                        typeSerializersMap,
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
    val secondLayerIndexes: List<Int?> = emptyList()
) {
    val contains: Boolean
        get() = index >= 0 || secondLayerIndexes.isNotEmpty()
}

private fun Collection<KSTypeParameter>.containsInTreeIndexOf(fieldType: KSType?): TypeContainsResult {
    if (fieldType == null) return TypeContainsResult(-1)
    val parentArgs = map { it.toString() }
    val index = parentArgs.indexOf(fieldType.toString().replace("?", ""))
    return if (index == -1) {
        val secondLayerIndexes = fieldType.arguments.map { argument ->
            parentArgs.indexOf(argument.type.toString())
        }
        TypeContainsResult(-1, secondLayerIndexes)
    } else {
        TypeContainsResult(index)
    }
}

private fun FunSpec.Builder.genericValSerializer(
    field: Field,
    typeSerializersMap: MutableMap<String, ValSerializer>,
    propertyToSerializerName: MutableMap<Field, FieldSerializer>,
    typeParameters: Set<KSTypeParameter>,
    firstType: KSTypeReference,
    secondType: KSTypeReference? = null,
) {
    val firstSerializer = renderValSerializer(typeSerializersMap, typeParameters, firstType)
    val secondSerializer = secondType?.let { renderValSerializer(typeSerializersMap, typeParameters, it) }
    propertyToSerializerName[field] = FieldSerializer(firstSerializer, secondSerializer)
}

private fun FunSpec.Builder.renderValSerializer(
    typeSerializersMap: MutableMap<String, ValSerializer>,
    typeParameters: Set<KSTypeParameter>,
    type: KSTypeReference
): ValSerializer {
    val typeName = type.toString()
    val indexResult = typeParameters.containsInTreeIndexOf(type.resolve())
    val serializerNotExist = !typeSerializersMap.contains(typeName)
    return if (indexResult.contains) {
        if (indexResult.index >= 0) {
            renderGenericSerializerVal(typeName, indexResult.index, serializerNotExist)
        } else {
            renderSerializerValWithGenericField(typeName, indexResult, serializerNotExist)
        }
    } else {
        renderSerializerVal(typeName, serializerNotExist)
    }.apply {
        typeSerializersMap[typeName] = this
    }
}

private fun FunSpec.Builder.renderSerializerVal(className: String, serializerNotExist: Boolean = true): ValSerializer {
    val valueName = className.toSerializerValName()
    if (serializerNotExist) {
        addStatement("val $valueName = GlobalSerializersLibrary.findSerializers(${className}::class)")
    }
    return ValSerializer(valueName)
}

private fun FunSpec.Builder.renderSerializerValWithGenericField(
    className: String,
    indexResult: TypeContainsResult,
    serializerNotExist: Boolean
): ValSerializer {
    val valueName = className.toSerializerValName()
    if (serializerNotExist) {
        addStatement("val $valueName = GlobalSerializersLibrary.findSerializers(${className}::class)")
    }
    val genericParameters = indexResult.secondLayerIndexes.joinToString { index ->
        if (index != null && index >= 0) {
            "genericTypeList[$index]"
        } else {
            "null"
        }
    }
    return ValSerializer(
        valueName,
        "listOf($genericParameters)"
    )
}

private fun FunSpec.Builder.renderGenericSerializerVal(
    className: String,
    index: Int,
    serializerNotExist: Boolean
): ValSerializer {
    val classNameLowCase = className.replaceFirstChar { it.lowercase() }
    val classTypeArguments = "${classNameLowCase}Args"
    val valueName = "${classNameLowCase}Serializer"
    if (serializerNotExist) {
        addStatement("val ${classNameLowCase}Type = genericTypeList[$index].type")
        addStatement("val $classTypeArguments = ${classNameLowCase}Type?.arguments.orEmpty()")
        addStatement("val $valueName = GlobalSerializersLibrary.findSerializers(${classNameLowCase}Type?.classifier as KClass<Any>)")
    }
    return ValSerializer(valueName, classTypeArguments)
}
