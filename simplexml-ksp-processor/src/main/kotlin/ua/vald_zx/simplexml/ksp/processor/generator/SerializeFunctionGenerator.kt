package ua.vald_zx.simplexml.ksp.processor.generator

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.*


internal fun FunSpec.Builder.generateSerialization(classToGenerate: ClassToGenerate): FunSpec.Builder {
    val serializersMap = generateAndGetSerializers(classToGenerate)
    beginControlFlow("tagFather.apply")
    renderChildren(classToGenerate.toDom(), serializersMap)
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
                    XmlUnitType.TAG -> {
                        if (element.property?.required == true) {
                            addStatement("val ${element.propertyName} = obj.${element.propertyName}?: throw SerializeException(\"\"\"field ${element.xmlName} value is required\"\"\")")
                            addStatement("$serializerName.buildXml(this, \"${element.xmlName}\", ${element.propertyName})")
                        } else {
                            beginControlFlow("obj.${element.propertyName}?.let")
                            addStatement("$serializerName.buildXml(this, \"${element.xmlName}\", it)")
                            endControlFlow()
                        }
                    }
                    XmlUnitType.ATTRIBUTE -> {
                        val serializeCall = "$serializerName.serialize(it)"
                        beginControlFlow("obj.${element.propertyName}?.let")
                        addStatement("attr(\"${element.xmlName}\", $serializeCall)")
                        endControlFlow()
                    }
                    XmlUnitType.LIST -> {
                        val entrySerializerName = serializersMap[element.property]
                        if (element.inlineList) {
                            beginControlFlow("obj.${element.propertyName}?.forEach")
                            addStatement("$entrySerializerName.buildXml(this, \"${element.entryName}\", it)")
                            endControlFlow()
                        } else {
                            beginControlFlow("obj.${element.propertyName}?.let")
                            addStatement("list ->")
                            beginControlFlow("tag(\"${element.xmlName}\") {")
                            beginControlFlow("list.forEach {")
                            addStatement("$entrySerializerName.buildXml(this, \"${element.entryName}\", it)")
                            endControlFlow()
                            endControlFlow()
                            endControlFlow()
                        }
                    }
                    else -> error("Not supported type ${element.type}")
                }
            }
        } else {
            if (element.children.isNotEmpty()) {
                if (element.propertyName.isEmpty()) {
                    beginControlFlow("tag(\"${element.xmlName}\")")
                } else {
                    beginControlFlow("$serializerName.buildXml(this, \"${element.xmlName}\", obj.${element.propertyName})")
                }
                renderChildren(element.children, serializersMap)
                endControlFlow()
            } else {
                when (element.type) {
                    XmlUnitType.TAG -> {
                        addStatement("${serializerName}.buildXml(this, \"${element.xmlName}\", obj.${element.propertyName})")
                    }
                    XmlUnitType.ATTRIBUTE -> {
                        val serializeCall = "$serializerName.serialize(obj.${element.propertyName})"
                        addStatement("attr(\"${element.xmlName}\", $serializeCall)")
                    }
                    XmlUnitType.LIST -> {
                        val entrySerializerName = serializersMap[element.property]
                        if (element.inlineList) {
                            printListForeach(element, entrySerializerName)
                        } else {
                            beginControlFlow("tag(\"${element.xmlName}\") {")
                            printListForeach(element, entrySerializerName)
                            endControlFlow()
                        }
                    }
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