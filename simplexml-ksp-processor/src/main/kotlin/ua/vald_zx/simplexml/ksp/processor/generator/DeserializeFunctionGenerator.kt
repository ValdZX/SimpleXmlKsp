package ua.vald_zx.simplexml.ksp.processor.generator

import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.DomElement
import ua.vald_zx.simplexml.ksp.processor.PropertyElement
import ua.vald_zx.simplexml.ksp.processor.XmlUnitType


internal fun FunSpec.Builder.generateDeserialization(
    classToGenerate: ClassToGenerate,
    logger: KSPLogger
): FunSpec.Builder {
    val className = classToGenerate.rootName
    logger.info("Generating deserialization method for $className")
    val serializersMap = generateAndGetSerializers(classToGenerate)
    addStatement("element as TagXmlElement?")
    val fieldToValueMap: MutableMap<String, String> = mutableMapOf()
    generateValues(classToGenerate.dom, fieldToValueMap, "element", 0)

    val anyGenerics = if (classToGenerate.typeParameters.isNotEmpty()) {
        buildString {
            append("<")
            append(classToGenerate.typeParameters.joinToString(", ") { "Any" })
            append(">")
        }
    } else ""
    addStatement("return ${classToGenerate.name}$anyGenerics(")
    val propertyElements = classToGenerate.propertyElements
    val propertiesRequiredToConstructor = propertyElements
        .filter { it.isConstructorParameter && !it.hasDefaultValue }
    propertiesRequiredToConstructor.forEach { property ->
        val name = property.propertyName
        val fieldSerializer = serializersMap[property] ?: error("Not found serializer")
        val entrySerializerName = fieldSerializer.serializerVariableName
        val genericTypesVariableName = fieldSerializer.genericTypesVariableName
        val argumentsFunArgument = if (genericTypesVariableName != null) {
            ", $genericTypesVariableName"
        } else ""
        if (property.xmlType == XmlUnitType.LIST) {
            beginControlFlow("$name = ${fieldToValueMap[name]}?.map")
            addDeserializeCallStatement("it", property, fieldSerializer, isNotNull = true)
            addStatement("$entrySerializerName.readData(it$argumentsFunArgument)")
            endControlFlow()
            addStatement("?: throw DeserializeException(\"\"\"$className field $name value is required\"\"\"),")
        } else {
            addDeserializeCallStatement(
                prefix = "$name = ",
                postfix = ",",
                fieldStatement = "${fieldToValueMap[name]}",
                property = property,
                fieldSerializer = fieldSerializer
            )
        }
    }
    val propertiesDynamics = propertyElements.subtract(propertiesRequiredToConstructor)
    if (propertiesDynamics.isEmpty()) {
        addStatement(")")
    } else {
        beginControlFlow(").apply")
        propertiesDynamics.forEach { property ->
            val name = property.propertyName
            val parsedValue = fieldToValueMap[name]
            if (property.xmlType == XmlUnitType.LIST) {
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

private fun FunSpec.Builder.generateValues(
    dom: List<DomElement>,
    fieldToValueMap: MutableMap<String, String>,
    parentValueName: String,
    layer: Int,
    iterator: Iterator<Int> = sequence {
        var counter = 0
        while (true) {
            yield(counter)
            counter++
        }
    }.iterator()
) {
    dom.forEach { element ->
        when (element.type) {
            XmlUnitType.TAG -> {
                val currentValueName = "layer${layer}Tag${iterator.next()}"
                addStatement("val $currentValueName = $parentValueName?.get(\"${element.xmlName}\")")
                fieldToValueMap[element.propertyName] = currentValueName
                generateValues(
                    element.children,
                    fieldToValueMap,
                    currentValueName,
                    layer + 1,
                    iterator
                )
            }
            XmlUnitType.ATTRIBUTE -> {
                val currentValueName = "layer${layer}Attribute${iterator.next()}"
                addStatement("val $currentValueName = $parentValueName?.attribute(\"${element.xmlName}\")")
                fieldToValueMap[element.propertyName] = currentValueName
            }
            XmlUnitType.LIST -> {
                val currentValueName = "layer${layer}Tag${iterator.next()}"
                if (element.inlineList) {
                    addStatement("val $currentValueName = $parentValueName?.getAll(\"${element.entryName}\")")
                    fieldToValueMap[element.propertyName] = currentValueName
                } else {
                    addStatement("val $currentValueName = $parentValueName?.get(\"${element.xmlName}\")")
                    val entryValuesName = "layer${layer}Tag${iterator.next()}"
                    addStatement("val $entryValuesName = $currentValueName?.getAll(\"${element.entryName}\")")
                    fieldToValueMap[element.propertyName] = entryValuesName
                    generateValues(
                        element.children.filter { it.type == XmlUnitType.ATTRIBUTE },
                        fieldToValueMap,
                        currentValueName,
                        layer + 1,
                        iterator
                    )
                }
            }
            else -> error("Not supported XmlUnitType ${element.type}")
        }
    }
}

private fun FunSpec.Builder.addDeserializeCallStatement(
    fieldStatement: String,
    property: PropertyElement,
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
        addStatement("$fieldStatement ?: throw DeserializeException(\"\"\"field ${property.propertyName} value is required\"\"\")$argumentsFunArgument")
        addStatement(")$postfix")
    } else if (isNotNull) {
        addStatement("$prefix$serializerName.readData($fieldStatement$argumentsFunArgument)$postfix")
    } else {
        addStatement("$prefix$fieldStatement?.let { $serializerName.readData(it$argumentsFunArgument) }$postfix")
    }
}