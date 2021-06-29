package ua.vald_zx.simplexml.ksp.processor.generator

import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.DomElement
import ua.vald_zx.simplexml.ksp.processor.XmlUnitType
import ua.vald_zx.simplexml.ksp.processor.toDom


internal fun FunSpec.Builder.generateDeserialization(
    classToGenerate: ClassToGenerate,
    logger: KSPLogger
): FunSpec.Builder {
    logger.info("Generating deserialization method for ${classToGenerate.rootName}")
    val statementBuilder = StringBuilder()
    val margin = "    "
    val dom = classToGenerate.toDom()
    statementBuilder.appendLine("val dom = raw.readXml()")
    val fieldToValueMap: MutableMap<String, String> = mutableMapOf()
    dom.generateValues(statementBuilder, fieldToValueMap, "dom", 0)
    statementBuilder.appendLine("return ${classToGenerate.name}(")
    val propertiesRequiredToConstructor =
        classToGenerate.propertyElements.filter { it.requiredToConstructor }
    val propertiesDynamics =
        classToGenerate.propertyElements.subtract(propertiesRequiredToConstructor)
    propertiesRequiredToConstructor.forEach { property ->
        val name = property.propertyName
        if (property.xmlType == XmlUnitType.LIST) {
            statementBuilder.appendLine("${margin}$name = ${fieldToValueMap[name]}?.map { it.text } ?: error(\"\"\"fields $name value is required\"\"\"),")
        } else {
            statementBuilder.appendLine("${margin}$name = ${fieldToValueMap[name]}?.text ?: error(\"\"\"fields $name value is required\"\"\"),")
        }
    }
    if (propertiesDynamics.isEmpty()) {
        statementBuilder.appendLine(")")
    } else {
        statementBuilder.appendLine(").apply {")
        propertiesDynamics.forEach { property ->
            val name = property.propertyName
            val parsedValue = fieldToValueMap[name]
            if (property.xmlType == XmlUnitType.LIST) {
                if (!property.required) {
                    statementBuilder.appendLine("${margin}if ($parsedValue != null) $name = $parsedValue.map { it.text }")
                } else {
                    statementBuilder.appendLine("${margin}$name = $parsedValue?.map { it.text } ?: error(\"\"\"fields $name value is required\"\"\")")
                }
            } else {
                if (!property.required) {
                    statementBuilder.appendLine("${margin}if ($parsedValue != null) $name = $parsedValue.text")
                } else {
                    statementBuilder.appendLine("${margin}$name = $parsedValue?.text ?: error(\"\"\"fields $name value is required\"\"\")")
                }
            }
        }
        statementBuilder.appendLine("}")
    }
    addStatement(statementBuilder.toString())
    return this
}

private fun List<DomElement>.generateValues(
    statementBuilder: StringBuilder,
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
    forEach { element ->
        when (element.type) {
            XmlUnitType.TAG -> {
                val currentValueName = "layer${layer}Tag${iterator.next()}"
                statementBuilder.appendLine("val $currentValueName = $parentValueName?.get(\"${element.xmlName}\")")
                fieldToValueMap[element.propertyName] = currentValueName
                element.children.generateValues(
                    statementBuilder,
                    fieldToValueMap,
                    currentValueName,
                    layer + 1,
                    iterator
                )
            }
            XmlUnitType.ATTRIBUTE -> {
                val currentValueName = "layer${layer}Attribute${iterator.next()}"
                statementBuilder.appendLine("val $currentValueName = $parentValueName?.attribute(\"${element.xmlName}\")")
                fieldToValueMap[element.propertyName] = currentValueName
            }
            XmlUnitType.LIST -> {
                val currentValueName = "layer${layer}Tag${iterator.next()}"
                if (element.inlineList) {
                    statementBuilder.appendLine("val $currentValueName = $parentValueName?.getAll(\"${element.entryName}\")")
                    fieldToValueMap[element.propertyName] = currentValueName
                } else {
                    statementBuilder.appendLine("val $currentValueName = $parentValueName?.get(\"${element.xmlName}\")")
                    val entryValuesName = "layer${layer}Tag${iterator.next()}"
                    statementBuilder.appendLine("val $entryValuesName = $currentValueName?.getAll(\"${element.entryName}\")")
                    fieldToValueMap[element.propertyName] = entryValuesName
                }
            }
            else -> error("Not supported XmlUnitType ${element.type}")
        }
    }
}