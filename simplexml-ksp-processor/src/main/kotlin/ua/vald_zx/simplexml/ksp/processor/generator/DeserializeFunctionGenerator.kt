package ua.vald_zx.simplexml.ksp.processor.generator

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.Field

internal fun FunSpec.Builder.generateDeserialization(
    classToGenerate: ClassToGenerate
): FunSpec.Builder {
    val className = classToGenerate.rootName
    val serializersMap = generateAndGetSerializers(classToGenerate)
    addStatement("element as TagXmlElement?")
    val fieldToValueMap: MutableMap<String, String> = mutableMapOf()
    generateValues(classToGenerate.dom, fieldToValueMap, "element", 0, sequence {
        var counter = 0
        while (true) {
            yield(counter)
            counter++
        }
    }.iterator())

    val anyGenerics = if (classToGenerate.typeParameters.isNotEmpty()) {
        buildString {
            append("<")
            append(classToGenerate.typeParameters.joinToString(", ") { "Any" })
            append(">")
        }
    } else ""
    addStatement("return ${classToGenerate.name}$anyGenerics(")
    val propertyElements = classToGenerate.fields
    val propertiesRequiredToConstructor = propertyElements
        .filter { it.isConstructorParameter && !it.hasDefaultValue }
    propertiesRequiredToConstructor.forEach { property ->
        val name = property.fieldName
        val fieldSerializer = serializersMap[property] ?: error("Not found serializer")
        when (property) {
            is Field.List -> {
                beginControlFlow("$name = ${fieldToValueMap[name]}?.map")
                addDeserializeCallStatement("it", property, fieldSerializer, isNotNull = true)
                endControlFlow()
                if (property.isMutableCollection) {
                    addStatement("?.toMutableList()")
                }
                addStatement("?: throw DeserializeException(\"\"\"$className field $name value is required\"\"\"),")
            }
            is Field.Map -> {
                val keySerializerName = fieldSerializer.serializerVariableName
                val valueSerializerName = fieldSerializer.valueSerializerVariableName
                val keyGenericTypesVariableName = fieldSerializer.genericTypesVariableName
                val keyArgumentsFunArgument = if (keyGenericTypesVariableName != null) {
                    ", $keyGenericTypesVariableName"
                } else ""
                val valueGenericTypesVariableName = fieldSerializer.valueGenericTypesVariableName
                val valueArgumentsFunArgument = if (valueGenericTypesVariableName != null) {
                    ", $valueGenericTypesVariableName"
                } else ""
                beginControlFlow("$name = ${fieldToValueMap[name]}?.map")
                addStatement("(keyElement, valueElement) ->")
                addStatement("val keyData = $keySerializerName.readData(keyElement$keyArgumentsFunArgument)")
                addStatement("val valueData = $valueSerializerName.readData(valueElement$valueArgumentsFunArgument)")
                addStatement("keyData to valueData")
                endControlFlow()
                addStatement("?.toMap() ?: throw DeserializeException(\"\"\"$className field $name value is required\"\"\")")
                if (property.isMutableCollection) {
                    addStatement("?.toMutableList()")
                }
                addStatement("?: throw DeserializeException(\"\"\"$className field $name value is required\"\"\"),")
            }
            else -> {
                addDeserializeCallStatement(
                    prefix = "$name = ",
                    postfix = ",",
                    fieldStatement = "${fieldToValueMap[name]}",
                    property = property,
                    fieldSerializer = fieldSerializer
                )
            }
        }
    }
    val propertiesDynamics = propertyElements.subtract(propertiesRequiredToConstructor)
    if (propertiesDynamics.isEmpty()) {
        addStatement(")")
    } else {
        beginControlFlow(").apply")
        propertiesDynamics.forEach { property ->
            val name = property.fieldName
            val parsedValue = fieldToValueMap[name]
            if (property is Field.List) {
                val fieldSerializer =
                    serializersMap[property] ?: error("Not found serializer")
                val entrySerializerName = fieldSerializer.serializerVariableName
                val genericTypesVariableName = fieldSerializer.genericTypesVariableName
                val argumentsFunArgument = if (genericTypesVariableName != null) {
                    ", $genericTypesVariableName"
                } else ""
                if (!property.required) {
                    beginControlFlow("if ($parsedValue != null)")
                    beginControlFlow("$name = $parsedValue.map")
                    addStatement("$entrySerializerName.readData(it$argumentsFunArgument)")
                    endControlFlow()
                    endControlFlow()
                } else {
                    beginControlFlow("$name = $parsedValue.map")
                    addStatement("$entrySerializerName.readData(it$argumentsFunArgument)")
                    endControlFlow()
                    addStatement("?: throw DeserializeException(\"\"\"$className field $name value is required\"\"\")")
                }
            } else if (property is Field.Map) {
                val fieldSerializer =
                    serializersMap[property] ?: error("Not found serializer")
                val keySerializerName = fieldSerializer.serializerVariableName
                val valueSerializerName = fieldSerializer.valueSerializerVariableName
                val keyGenericTypesVariableName = fieldSerializer.genericTypesVariableName
                val keyArgumentsFunArgument = if (keyGenericTypesVariableName != null) {
                    ", $keyGenericTypesVariableName"
                } else ""
                val valueGenericTypesVariableName = fieldSerializer.valueGenericTypesVariableName
                val valueArgumentsFunArgument = if (valueGenericTypesVariableName != null) {
                    ", $valueGenericTypesVariableName"
                } else ""
                if (!property.required) {
                    beginControlFlow("if ($parsedValue != null)")
                    beginControlFlow("$name = $parsedValue.map")
                    addStatement("(keyElement, valueElement) ->")
                    addStatement("val keyData = $keySerializerName.readData(keyElement$keyArgumentsFunArgument)")
                    addStatement("val valueData = $valueSerializerName.readData(valueElement$valueArgumentsFunArgument)")
                    addStatement("keyData to valueData")
                    endControlFlow()
                    addStatement(".toMap()")
                    endControlFlow()
                } else {
                    beginControlFlow("$name = $parsedValue?.map")
                    addStatement("(keyElement, valueElement) ->")
                    addStatement("val keyData = $keySerializerName.readData(keyElement$keyArgumentsFunArgument)")
                    addStatement("val valueData = $valueSerializerName.readData(valueElement$valueArgumentsFunArgument)")
                    addStatement("keyData to valueData")
                    endControlFlow()
                    addStatement("?.toMap() ?: throw DeserializeException(\"\"\"$className field $name value is required\"\"\")")
                }
            } else {
                val serializerName = serializersMap[property] ?: error("Not found serializer")
                if (!property.required) {
                    beginControlFlow("if ($parsedValue != null)")
                    addDeserializeCallStatement("$parsedValue", property, serializerName, "$name = ", isNotNull = true)
                    endControlFlow()
                } else {
                    addDeserializeCallStatement("$parsedValue", property, serializerName, "$name = ")
                }
            }
        }
        endControlFlow()
    }
    return this
}

fun FunSpec.Builder.generateValues(
    dom: List<Field>,
    fieldToValueMap: MutableMap<String, String>,
    parentValueName: String,
    layer: Int,
    numberIterator: Iterator<Int>
) {
    dom.forEach { field ->
        field.generator.renderDeserializationVariable(this, fieldToValueMap, parentValueName, layer, numberIterator)
    }
}

private fun FunSpec.Builder.addDeserializeCallStatement(
    fieldStatement: String,
    property: Field,
    fieldSerializer: FieldSerializer,
    prefix: String = "",
    postfix: String = "",
    isNotNull: Boolean = false
) {
    val serializerName = fieldSerializer.serializerVariableName
    val genericTypesVariableName = fieldSerializer.genericTypesVariableName
    val argumentsFunArgument = if (genericTypesVariableName != null) {
        ", $genericTypesVariableName"
    } else ""

    if (property.required && !isNotNull) {
        addStatement("$prefix$serializerName.readData(")
        addStatement("$fieldStatement ?: throw DeserializeException(\"\"\"field ${property.fieldName} value is required\"\"\")$argumentsFunArgument")
        addStatement(")$postfix")
    } else if (isNotNull) {
        addStatement("$prefix$serializerName.readData($fieldStatement$argumentsFunArgument)$postfix")
    } else {
        addStatement("$prefix$fieldStatement?.let { $serializerName.readData(it$argumentsFunArgument) }$postfix")
    }
}