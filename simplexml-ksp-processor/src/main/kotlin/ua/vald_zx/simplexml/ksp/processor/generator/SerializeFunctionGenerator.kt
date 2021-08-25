package ua.vald_zx.simplexml.ksp.processor.generator

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.DomElement
import ua.vald_zx.simplexml.ksp.processor.PropertyElement
import ua.vald_zx.simplexml.ksp.processor.XmlUnitType


internal fun FunSpec.Builder.generateSerialization(classToGenerate: ClassToGenerate): FunSpec.Builder {
    val serializersMap = generateAndGetSerializers(classToGenerate)
    beginControlFlow("tagFather.apply")
    renderChildren(classToGenerate.dom, serializersMap)
    endControlFlow()
    return this
}

private fun FunSpec.Builder.renderChildren(
    elements: Iterable<DomElement>,
    serializersMap: Map<PropertyElement, FieldSerializer>
) {
    elements.forEach { element ->
        val fieldSerializer = serializersMap[element.property]
        val serializerName = fieldSerializer?.serializerVariableName
        val genericTypesVariableName = fieldSerializer?.genericTypesVariableName
        val argumentsFunArgument = if (genericTypesVariableName != null) {
            ", $genericTypesVariableName"
        } else ""
        if (element.isNullable) {
            if (element.children.isNotEmpty()) {
                addStatement("val ${element.propertyName} = obj.${element.propertyName}")
                beginControlFlow("if (${element.propertyName} != null)")
                beginControlFlow("$serializerName.buildXml(this, \"${element.xmlName}\", ${element.propertyName}$argumentsFunArgument)")
                renderChildren(element.children, serializersMap)
                endControlFlow()
                nextControlFlow("else")
                beginControlFlow("tag(\"${element.xmlName}\")")
                renderChildren(element.children, serializersMap)
                endControlFlow()
                endControlFlow()
            } else {
                when (element.type) {
                    XmlUnitType.TAG -> tagNullableValue(serializerName, argumentsFunArgument, element)
                    XmlUnitType.ATTRIBUTE -> attributeNullableValue(serializerName, element)
                    XmlUnitType.LIST -> listNullableValue(serializerName, argumentsFunArgument, element)
                    XmlUnitType.MAP -> mapNullableValue(fieldSerializer, element)
                    else -> error("Not supported type ${element.type}")
                }
            }
        } else {
            if (element.children.isNotEmpty()) {
                when (element.type) {
                    XmlUnitType.TAG -> tagWithChildren(serializerName, argumentsFunArgument, element, serializersMap)
                    XmlUnitType.LIST -> listWithAttributes(
                        serializerName,
                        argumentsFunArgument,
                        element,
                        serializersMap
                    )
                    XmlUnitType.MAP -> mapWithAttributes(fieldSerializer, element, serializersMap)
                    else -> error("Not supported XmlUnitType ${element.type}")
                }
            } else {
                when (element.type) {
                    XmlUnitType.TAG -> tag(serializerName, argumentsFunArgument, element)
                    XmlUnitType.ATTRIBUTE -> attribute(serializerName, element)
                    XmlUnitType.LIST -> list(serializerName, argumentsFunArgument, element)
                    XmlUnitType.MAP -> map(fieldSerializer, element)
                    else -> error("Not supported XmlUnitType ${element.type}")
                }
            }
        }
    }
}

private fun FunSpec.Builder.printListForeach(
    element: DomElement,
    entrySerializerName: String?,
    argumentsFunArgument: String
) {
    beginControlFlow("obj.${element.propertyName}.forEach")
    addStatement("$entrySerializerName.buildXml(this, \"${element.entryName}\", it$argumentsFunArgument)")
    endControlFlow()
}

private fun FunSpec.Builder.printMapForeach(
    element: DomElement,
    keySerializerName: String?,
    keyArgumentsFunArgument: String?,
    valueSerializerName: String?,
    valueArgumentsFunArgument: String?,
    propertyName: String
) {
    beginControlFlow("$propertyName.forEach")
    addStatement("(key, value) ->")
    addStatement("$keySerializerName.buildXml(this, \"${element.keyName}\", key${keyArgumentsFunArgument.orEmpty()})")
    addStatement("$valueSerializerName.buildXml(this, \"${element.valueName}\", value${valueArgumentsFunArgument.orEmpty()})")
    endControlFlow()
}

private fun FunSpec.Builder.tag(serializerName: String?, argumentsFunArgument: String, element: DomElement) {
    addStatement("${serializerName}.buildXml(this, \"${element.xmlName}\", obj.${element.propertyName})$argumentsFunArgument")
}

private fun FunSpec.Builder.tagWithChildren(
    serializerName: String?,
    argumentsFunArgument: String,
    element: DomElement,
    serializersMap: Map<PropertyElement, FieldSerializer>
) {
    if (element.propertyName.isEmpty()) {
        beginControlFlow("tag(\"${element.xmlName}\")")
    } else {
        beginControlFlow("$serializerName.buildXml(this, \"${element.xmlName}\", obj.${element.propertyName}$argumentsFunArgument)")
    }
    renderChildren(element.children, serializersMap)
    endControlFlow()
}

private fun FunSpec.Builder.tagNullableValue(
    serializerName: String?,
    argumentsFunArgument: String,
    element: DomElement
) {
    if (element.property?.required == true) {
        addStatement("val ${element.propertyName} = obj.${element.propertyName}?: throw SerializeException(\"\"\"field ${element.xmlName} value is required\"\"\")")
        addStatement("$serializerName.buildXml(this, \"${element.xmlName}\", ${element.propertyName}$argumentsFunArgument)")
    } else {
        beginControlFlow("obj.${element.propertyName}?.let")
        addStatement("$serializerName.buildXml(this, \"${element.xmlName}\", it$argumentsFunArgument)")
        endControlFlow()
    }
}

private fun FunSpec.Builder.attribute(serializerName: String?, element: DomElement) {
    val serializeCall = "$serializerName.serialize(obj.${element.propertyName})"
    addStatement("attr(\"${element.xmlName}\", $serializeCall)")
}

private fun FunSpec.Builder.attributeNullableValue(serializerName: String?, element: DomElement) {
    val serializeCall = "$serializerName.serialize(it)"
    beginControlFlow("obj.${element.propertyName}?.let")
    addStatement("attr(\"${element.xmlName}\", $serializeCall)")
    endControlFlow()
}

private fun FunSpec.Builder.list(serializerName: String?, argumentsFunArgument: String, element: DomElement) {
    if (element.inlineList) {
        printListForeach(element, serializerName, argumentsFunArgument)
    } else {
        beginControlFlow("tag(\"${element.xmlName}\") {")
        printListForeach(element, serializerName, argumentsFunArgument)
        endControlFlow()
    }
}

private fun FunSpec.Builder.map(fieldSerializer: FieldSerializer?, element: DomElement) {
    val keySerializerName = fieldSerializer?.serializerVariableName
    val keyGenericTypesVariableName = fieldSerializer?.genericTypesVariableName
    val valueSerializerVariableName = fieldSerializer?.valueSerializerVariableName
    val valueGenericTypesVariableName = fieldSerializer?.valueGenericTypesVariableName
    if (!element.inlineList) {
        beginControlFlow("tag(\"${element.xmlName}\") {")
    }
    printMapForeach(
        element,
        keySerializerName,
        keyGenericTypesVariableName,
        valueSerializerVariableName,
        valueGenericTypesVariableName,
        "obj.${element.propertyName}"
    )
    if (!element.inlineList) {
        endControlFlow()
    }
}

private fun FunSpec.Builder.listWithAttributes(
    serializerName: String?,
    argumentsFunArgument: String,
    element: DomElement,
    serializersMap: Map<PropertyElement, FieldSerializer>
) {
    if (element.inlineList) {
        printListForeach(element, serializerName, argumentsFunArgument)
    } else {
        beginControlFlow("tag(\"${element.xmlName}\") {")
        renderChildren(element.children, serializersMap)
        printListForeach(element, serializerName, argumentsFunArgument)
        endControlFlow()
    }
}

private fun FunSpec.Builder.mapWithAttributes(
    fieldSerializer: FieldSerializer?,
    element: DomElement,
    serializersMap: Map<PropertyElement, FieldSerializer>
) {
    val keySerializerName = fieldSerializer?.serializerVariableName
    val keyGenericTypesVariableName = fieldSerializer?.genericTypesVariableName
    val valueSerializerVariableName = fieldSerializer?.valueSerializerVariableName
    val valueGenericTypesVariableName = fieldSerializer?.valueGenericTypesVariableName
    if (!element.inlineList) {
        beginControlFlow("tag(\"${element.xmlName}\") {")
        renderChildren(element.children, serializersMap)
    }
    printMapForeach(
        element,
        keySerializerName,
        keyGenericTypesVariableName,
        valueSerializerVariableName,
        valueGenericTypesVariableName,
        "obj.${element.propertyName}"
    )
    if (!element.inlineList) {
        endControlFlow()
    }
}

private fun FunSpec.Builder.listNullableValue(
    serializerName: String?,
    argumentsFunArgument: String,
    element: DomElement
) {
    if (element.inlineList) {
        beginControlFlow("obj.${element.propertyName}?.forEach")
        addStatement("$serializerName.buildXml(this, \"${element.entryName}\", it$argumentsFunArgument)")
        endControlFlow()
    } else {
        beginControlFlow("obj.${element.propertyName}?.let")
        addStatement("list ->")
        beginControlFlow("tag(\"${element.xmlName}\") {")
        beginControlFlow("list.forEach {")
        addStatement("$serializerName.buildXml(this, \"${element.entryName}\", it$argumentsFunArgument)")
        endControlFlow()
        endControlFlow()
        endControlFlow()
    }
}

private fun FunSpec.Builder.mapNullableValue(fieldSerializer: FieldSerializer?, element: DomElement) {
    val keySerializerName = fieldSerializer?.serializerVariableName
    val keyGenericTypesVariableName = fieldSerializer?.genericTypesVariableName
    val valueSerializerVariableName = fieldSerializer?.valueSerializerVariableName
    val valueGenericTypesVariableName = fieldSerializer?.valueGenericTypesVariableName

    if (element.inlineList) {
        beginControlFlow("obj.${element.propertyName}?.forEach")
        printMapForeach(
            element,
            keySerializerName,
            keyGenericTypesVariableName,
            valueSerializerVariableName,
            valueGenericTypesVariableName,
            "it"
        )
        endControlFlow()
    } else {
        beginControlFlow("obj.${element.propertyName}?.let")
        addStatement("map ->")
        beginControlFlow("tag(\"${element.xmlName}\") {")
        printMapForeach(
            element,
            keySerializerName,
            keyGenericTypesVariableName,
            valueSerializerVariableName,
            valueGenericTypesVariableName,
            "map"
        )
        endControlFlow()
        endControlFlow()
    }
}