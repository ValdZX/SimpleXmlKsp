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
    val genericTypesVariableName: String? = null
)

fun FunSpec.Builder.generateAndGetSerializers(classToGenerate: ClassToGenerate): Map<PropertyElement, FieldSerializer> {
    val propertyToSerializerName = mutableMapOf<PropertyElement, FieldSerializer>()
    val propertyElements = classToGenerate.propertyElements
    val genericsProperties = mutableListOf<PropertyElement>()
    val typeParameters = classToGenerate.typeParameters
    if (typeParameters.isNotEmpty()) {
        typeParameters.forEachIndexed { index, type ->
            val typeName = type.toString()
            val propertyElement = propertyElements.find { it.propertyType.toString() == typeName }
            if (propertyElement != null) {
                genericsProperties.add(propertyElement)
                val typeForVal = typeName.replaceFirstChar { it.lowercase() }
                addStatement("val ${typeForVal}Type = genericTypeList[$index].type")
                addStatement("val ${typeForVal}Args = ${typeForVal}Type?.arguments.orEmpty()")
                addStatement("val ${typeForVal}Serializer = GlobalSerializersLibrary.findSerializers(${typeForVal}Type?.classifier as KClass<Any>)")
                val fieldSerializer = FieldSerializer("${typeForVal}Serializer", "${typeForVal}Args")
                propertyToSerializerName[propertyElement] = fieldSerializer
            }
        }
    }
    val objectProperties = (propertyElements - genericsProperties)
        .filter { property -> property.converterType == null }
    val propertyToTypeSerializer = objectProperties
        .associateWith { property ->
            var className = property.propertyType.toString()
            if (className == "List") {
                className = property.propertyEntryType.toString()
            }
            SerializerDeclaration(className, className.replaceFirstChar { it.lowercase() } + "Serializer")
        }
    propertyToSerializerName.putAll(propertyToTypeSerializer.mapValues { (_, declaration) ->
        FieldSerializer(declaration.valueName)
    })
    propertyToTypeSerializer.values.toSet().forEach { serializerDeclaration ->
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