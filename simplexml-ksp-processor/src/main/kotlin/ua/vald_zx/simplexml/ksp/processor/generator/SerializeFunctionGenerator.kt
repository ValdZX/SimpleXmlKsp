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
    serializersMap: Map<PropertyElement, String>
) {
    elements.forEach { element ->
        val serializerName = serializersMap[element.property]
        if (element.isNullable) {
            if (element.children.isNotEmpty()) {
                addStatement("val ${element.propertyName} = obj.${element.propertyName}")
                beginControlFlow("if (${element.propertyName} != null)")
                beginControlFlow("$serializerName.buildXml(this, \"${element.xmlName}\", ${element.propertyName})")
                renderChildren(element.children, serializersMap)
                endControlFlow()
                nextControlFlow("else")
                beginControlFlow("tag(\"${element.xmlName}\")")
                renderChildren(element.children, serializersMap)
                endControlFlow()
                endControlFlow()
            } else {
                when (element.type) {
                    XmlUnitType.TAG -> tagNullableValue(serializerName, element)
                    XmlUnitType.ATTRIBUTE -> attributeNullableValue(serializerName, element)
                    XmlUnitType.LIST -> listNullableValue(serializerName, element)
                    else -> error("Not supported type ${element.type}")
                }
            }
        } else {
            if (element.children.isNotEmpty()) {
                when (element.type) {
                    XmlUnitType.TAG -> tagWithChildren(serializerName, element, serializersMap)
                    XmlUnitType.LIST -> listWithAttributes(serializerName, element, serializersMap)
                    else -> error("Not supported XmlUnitType ${element.type}")
                }
            } else {
                when (element.type) {
                    XmlUnitType.TAG -> tag(serializerName, element)
                    XmlUnitType.ATTRIBUTE -> attribute(serializerName, element)
                    XmlUnitType.LIST -> list(serializerName, element)
                    else -> error("Not supported XmlUnitType ${element.type}")
                }
            }
        }
    }
}

private fun FunSpec.Builder.printListForeach(element: DomElement, entrySerializerName: String?) {
    beginControlFlow("obj.${element.propertyName}.forEach")
    addStatement("$entrySerializerName.buildXml(this, \"${element.entryName}\", it)")
    endControlFlow()
}

private fun FunSpec.Builder.tag(serializerName: String?, element: DomElement) {
    addStatement("${serializerName}.buildXml(this, \"${element.xmlName}\", obj.${element.propertyName})")
}

private fun FunSpec.Builder.tagWithChildren(
    serializerName: String?,
    element: DomElement,
    serializersMap: Map<PropertyElement, String>
) {
    if (element.propertyName.isEmpty()) {
        beginControlFlow("tag(\"${element.xmlName}\")")
    } else {
        beginControlFlow("$serializerName.buildXml(this, \"${element.xmlName}\", obj.${element.propertyName})")
    }
    renderChildren(element.children, serializersMap)
    endControlFlow()
}

private fun FunSpec.Builder.tagNullableValue(serializerName: String?, element: DomElement) {
    if (element.property?.required == true) {
        addStatement("val ${element.propertyName} = obj.${element.propertyName}?: throw SerializeException(\"\"\"field ${element.xmlName} value is required\"\"\")")
        addStatement("$serializerName.buildXml(this, \"${element.xmlName}\", ${element.propertyName})")
    } else {
        beginControlFlow("obj.${element.propertyName}?.let")
        addStatement("$serializerName.buildXml(this, \"${element.xmlName}\", it)")
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

private fun FunSpec.Builder.list(serializerName: String?, element: DomElement) {
    if (element.inlineList) {
        printListForeach(element, serializerName)
    } else {
        beginControlFlow("tag(\"${element.xmlName}\") {")
        printListForeach(element, serializerName)
        endControlFlow()
    }
}

private fun FunSpec.Builder.listWithAttributes(
    serializerName: String?,
    element: DomElement,
    serializersMap: Map<PropertyElement, String>
) {
    if (element.inlineList) {
        printListForeach(element, serializerName)
    } else {
        beginControlFlow("tag(\"${element.xmlName}\") {")
        renderChildren(element.children, serializersMap)
        printListForeach(element, serializerName)
        endControlFlow()
    }
}

private fun FunSpec.Builder.listNullableValue(serializerName: String?, element: DomElement) {
    if (element.inlineList) {
        beginControlFlow("obj.${element.propertyName}?.forEach")
        addStatement("$serializerName.buildXml(this, \"${element.entryName}\", it)")
        endControlFlow()
    } else {
        beginControlFlow("obj.${element.propertyName}?.let")
        addStatement("list ->")
        beginControlFlow("tag(\"${element.xmlName}\") {")
        beginControlFlow("list.forEach {")
        addStatement("$serializerName.buildXml(this, \"${element.entryName}\", it)")
        endControlFlow()
        endControlFlow()
        endControlFlow()
    }
}