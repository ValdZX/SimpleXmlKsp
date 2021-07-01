package ua.vald_zx.simplexml.ksp.processor.generator

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.DomElement
import ua.vald_zx.simplexml.ksp.processor.XmlUnitType
import ua.vald_zx.simplexml.ksp.processor.toDom


internal fun FunSpec.Builder.generateSerialization(classToGenerate: ClassToGenerate): FunSpec.Builder {
    val statementBuilder = StringBuilder()
    statementBuilder.appendLine("return tag(\"${classToGenerate.rootName}\") {")
    classToGenerate.toDom().renderChildren(statementBuilder, 1)
    statementBuilder.appendLine("}.render()")
    addStatement(statementBuilder.toString())
    return this
}

private fun Iterable<DomElement>.renderChildren(builder: StringBuilder, offset: Int) {
    val currentMargin = " ".repeat(offset * 4)
    val oneDeeperMargin = " ".repeat((offset + 1) * 4)
    val twoDeeperMargin = " ".repeat((offset + 2) * 4)
    forEach { element ->
        if (element.isNullable) {
            if (element.children.isNotEmpty()) {
                val childrenPrint = StringBuilder()
                element.children.renderChildren(childrenPrint, offset + 2)
                builder.appendLine("${currentMargin}val ${element.propertyName} = obj.${element.propertyName}")
                builder.appendLine("${currentMargin}if (${element.propertyName} != null) {")
                builder.appendLine("${oneDeeperMargin}tag(\"${element.xmlName}\", ${element.propertyName}) {")
                builder.appendLine(childrenPrint)
                builder.appendLine("${oneDeeperMargin}}")
                builder.appendLine("${currentMargin}} else {")
                builder.appendLine("${oneDeeperMargin}tag(\"${element.xmlName}\") {")
                builder.appendLine(childrenPrint)
                builder.appendLine("${oneDeeperMargin}}")
                builder.appendLine("${currentMargin}}")
            } else {
                if (element.type == XmlUnitType.TAG) {
                    builder.appendLine("${currentMargin}obj.${element.propertyName}?.let { tag(\"${element.xmlName}\", it) }")
                } else if (element.type == XmlUnitType.ATTRIBUTE) {
                    builder.appendLine("${currentMargin}obj.${element.propertyName}?.let { attr(\"${element.xmlName}\", it) }")
                }
            }
        } else {
            if (element.children.isNotEmpty()) {
                if (element.propertyName.isEmpty()) {
                    builder.appendLine("${currentMargin}tag(\"${element.xmlName}\") {")
                } else {
                    builder.appendLine("${currentMargin}tag(\"${element.xmlName}\", obj.${element.propertyName}) {")
                }
                element.children.renderChildren(builder, offset + 1)
                builder.appendLine("${currentMargin}}")
            } else {
                when (element.type) {
                    XmlUnitType.TAG -> {
                        builder.appendLine("${currentMargin}tag(\"${element.xmlName}\", obj.${element.propertyName})")
                    }
                    XmlUnitType.ATTRIBUTE -> {
                        builder.appendLine("${currentMargin}attr(\"${element.xmlName}\", obj.${element.propertyName})")
                    }
                    XmlUnitType.LIST -> {
                        if (element.inlineList) {
                            builder.printListForeach(currentMargin, element, oneDeeperMargin)
                        } else {
                            builder.appendLine("${currentMargin}tag(\"${element.xmlName}\") {")
                            builder.printListForeach(oneDeeperMargin, element, twoDeeperMargin)
                            builder.appendLine("${currentMargin}}")
                        }
                    }
                    else -> error("Not supported XmlUnitType ${element.type}")
                }
            }
        }
    }
}

private fun StringBuilder.printListForeach(
    currentMargin: String,
    unit: DomElement,
    oneDeeperMargin: String
) {
    appendLine("${currentMargin}obj.${unit.propertyName}.forEach {")
    appendLine("${oneDeeperMargin}tag(\"${unit.entryName}\", it)")
    appendLine("${currentMargin}}")
}