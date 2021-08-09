package ua.vald_zx.simplexml.ksp.processor.generator

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.PropertyElement

data class SerializerDeclaration(
    val className: String,
    val valueName: String
)

fun FunSpec.Builder.generateAndGetSerializers(classToGenerate: ClassToGenerate): Map<PropertyElement, String> {
    val propertyToSerializerName = mutableMapOf<PropertyElement, String>()
    val propertyToTypeSerializer = classToGenerate.propertyElements
        .filter { property -> property.converterType == null }
        .associateWith { property ->
            var className = property.propertyType.toString()
            if(className == "List") {
                className = property.propertyEntryType.toString()
            }
            SerializerDeclaration(className, className.replaceFirstChar { it.lowercase() } + "Serializer")
        }
    propertyToSerializerName.putAll(propertyToTypeSerializer.mapValues { (_, declaration) -> declaration.valueName })
    propertyToTypeSerializer.values.toSet().forEach { serializerDeclaration ->
        addStatement("val ${serializerDeclaration.valueName} = GlobalSerializersLibrary.findSerializers(${serializerDeclaration.className}::class)")
    }

    val propertyToConverterSerializer = classToGenerate.propertyElements
        .filter { property -> property.converterType != null }
        .associateWith { property ->
            val converterType = property.converterType ?: error("Unknown error")
            val className = converterType.declaration.simpleName.asString()
            SerializerDeclaration(className, className.replaceFirstChar { it.lowercase() } + "Serializer")
        }
    propertyToSerializerName.putAll(propertyToConverterSerializer.mapValues { (_, declaration) -> declaration.valueName })
    propertyToConverterSerializer.values.toSet().forEach { serializerDeclaration ->
        addStatement("val ${serializerDeclaration.valueName} = ValueSerializer(${serializerDeclaration.className}())")
    }
    return propertyToSerializerName
}