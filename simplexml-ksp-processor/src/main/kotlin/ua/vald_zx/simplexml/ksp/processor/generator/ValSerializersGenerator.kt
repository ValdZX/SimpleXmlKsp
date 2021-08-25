package ua.vald_zx.simplexml.ksp.processor.generator

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.PropertyElement

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

fun FunSpec.Builder.generateAndGetSerializers(classToGenerate: ClassToGenerate): Map<PropertyElement, FieldSerializer> {
    val propertyToSerializerName = mutableMapOf<PropertyElement, FieldSerializer>()
    val propertyElements = classToGenerate.propertyElements
    val genericsProperties = mutableListOf<PropertyElement>()
    val typeParameters = classToGenerate.typeParameters.toSet()
    if (typeParameters.isNotEmpty()) {
        val typeMap = mutableMapOf<String, FieldSerializer>()
        typeParameters.forEachIndexed { index, type ->
            val typeName = type.toString()
            propertyElements.filter { it.propertyType.toString() == typeName }.forEach { propertyElement ->
                genericsProperties.add(propertyElement)
                val cachedFieldSerializer = typeMap[propertyElement.propertyType.toString()]
                if (cachedFieldSerializer == null) {
                    val typeForVal = typeName.replaceFirstChar { it.lowercase() }
                    addStatement("val ${typeForVal}Type = genericTypeList[$index].type")
                    addStatement("val ${typeForVal}Args = ${typeForVal}Type?.arguments.orEmpty()")
                    addStatement("val ${typeForVal}Serializer = GlobalSerializersLibrary.findSerializers(${typeForVal}Type?.classifier as KClass<Any>)")
                    val fieldSerializer = FieldSerializer("${typeForVal}Serializer", "${typeForVal}Args")
                    propertyToSerializerName[propertyElement] = fieldSerializer
                    typeMap[propertyElement.propertyType.toString()] = fieldSerializer
                } else {
                    propertyToSerializerName[propertyElement] = cachedFieldSerializer
                }
            }
        }
    }
    val objectProperties = (propertyElements - genericsProperties)
        .filter { property -> property.converterType == null }
    val propertyToTypeSerializer = objectProperties
        .associateWith { property ->
            when (val className = property.propertyType.toString()) {
                "List", "MutableList" -> {
                    val entryClassName = property.propertyEntryType.toString()
                    val declaration = SerializerDeclaration(
                        entryClassName,
                        entryClassName.replaceFirstChar { it.lowercase() } + "Serializer")
                    listOf(declaration)
                }
                "Map", "MutableMap" -> {
                    val keyClassName = property.propertyKeyType.toString()
                    val valueClassName = property.propertyValueType.toString()
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