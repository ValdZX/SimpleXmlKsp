package ua.vald_zx.simplexml.ksp.processor.generator

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.Field

data class SerializerDeclaration(
    val className: String,
    val valueName: String
)

data class FieldSerializer(
    val serializerVariableName: String,
    val genericTypesVariableName: String? = null,
    val valueSerializerVariableName: String? = null,
    val valueGenericTypesVariableName: String? = null
)

fun FunSpec.Builder.generateAndGetSerializers(classToGenerate: ClassToGenerate): Map<Field, FieldSerializer> {
    val propertyToSerializerName = mutableMapOf<Field, FieldSerializer>()
    val propertyElements = classToGenerate.fields
    val genericsProperties = mutableListOf<Field>()
    val typeParameters = classToGenerate.typeParameters.toSet()
    if (typeParameters.isNotEmpty()) {
        val typeMap = mutableMapOf<String, FieldSerializer>()
        typeParameters.forEachIndexed { index, type ->
            val typeName = type.toString()
            propertyElements.filter { it.fieldType?.toString() == typeName }.forEach { field ->
                genericsProperties.add(field)
                val fieldType = field.fieldType
                val cachedFieldSerializer = typeMap[fieldType.toString()]
                if (cachedFieldSerializer == null) {
                    val typeForVal = typeName.replaceFirstChar { it.lowercase() }
                    addStatement("val ${typeForVal}Type = genericTypeList[$index].type")
                    addStatement("val ${typeForVal}Args = ${typeForVal}Type?.arguments.orEmpty()")
                    addStatement("val ${typeForVal}Serializer = GlobalSerializersLibrary.findSerializers(${typeForVal}Type?.classifier as KClass<Any>)")
                    val fieldSerializer = FieldSerializer("${typeForVal}Serializer", "${typeForVal}Args")
                    propertyToSerializerName[field] = fieldSerializer
                    typeMap[fieldType.toString()] = fieldSerializer
                } else {
                    propertyToSerializerName[field] = cachedFieldSerializer
                }
            }
        }
    }
    val objectProperties = (propertyElements - genericsProperties)
        .filter { property -> property.converterType == null }
    val propertyToTypeSerializer = objectProperties
        .associateWith { property ->
            when (property) {
                is Field.List -> {
                    val entryClassName = property.entryType.toString()
                    val declaration = SerializerDeclaration(
                        entryClassName,
                        entryClassName.replaceFirstChar { it.lowercase() } + "Serializer")
                    listOf(declaration)
                }
                is Field.Map -> {
                    val keyClassName = property.keyType.toString()
                    val valueClassName = property.entryType.toString()
                    val keyDeclaration = SerializerDeclaration(
                        keyClassName,
                        keyClassName.replaceFirstChar { it.lowercase() } + "Serializer",
                    )
                    val valueDeclaration = SerializerDeclaration(
                        valueClassName,
                        valueClassName.replaceFirstChar { it.lowercase() } + "Serializer",
                    )
                    listOf(keyDeclaration, valueDeclaration)
                }
                else -> {
                    val className = property.fieldType.toString()
                    val declaration = SerializerDeclaration(
                        className,
                        className.replaceFirstChar { it.lowercase() } + "Serializer")
                    listOf(declaration)
                }
            }
        }
    propertyToSerializerName.putAll(propertyToTypeSerializer.mapValues { (_, declarations) ->
        val declaration = declarations[0]
        val valueDeclaration = declarations.getOrNull(1)
        FieldSerializer(
            serializerVariableName = declaration.valueName,
            valueSerializerVariableName = valueDeclaration?.valueName
        )
    })
    propertyToTypeSerializer.values.flatten().toSet().forEach { serializerDeclaration ->
        addStatement("val ${serializerDeclaration.valueName} = GlobalSerializersLibrary.findSerializers(${serializerDeclaration.className}::class)")
    }

    val propertiesWithConverter = (propertyElements - objectProperties - genericsProperties)
        .filter { property -> property.converterType != null }
    val propertyToConverterSerializer = propertiesWithConverter
        .associateWith { property ->
            val converterType = property.converterType ?: error("Unknown error")
            val className = converterType.declaration.simpleName.asString()
            SerializerDeclaration(className, className.replaceFirstChar { it.lowercase() } + "Serializer")
        }
    propertyToSerializerName.putAll(propertyToConverterSerializer.mapValues { (_, declaration) ->
        FieldSerializer(declaration.valueName)
    })
    propertyToConverterSerializer.values.toSet().forEach { serializerDeclaration ->
        addStatement("val ${serializerDeclaration.valueName} = ValueSerializer(${serializerDeclaration.className}())")
    }
    return propertyToSerializerName
}