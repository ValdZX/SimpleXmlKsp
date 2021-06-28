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
    val margin = " ".repeat(offset * 4)
    val submargin = " ".repeat((offset + 1) * 4)
    forEach { unit ->
        if (unit.isNullable) {
            if (unit.children.isNotEmpty()) {
                builder.appendLine("${margin}val ${unit.propertyName} = obj.${unit.propertyName}")
                builder.appendLine("${margin}if (${unit.propertyName} != null) {")
                builder.appendLine("${submargin}tag(\"${unit.xmlName}\", ${unit.propertyName}) {")
                unit.children.renderChildren(builder, offset + 2)
                builder.appendLine("${submargin}}")
                builder.appendLine("${margin}} else {")
                builder.appendLine("${submargin}tag(\"${unit.xmlName}\") {")
                unit.children.renderChildren(builder, offset + 2)
                builder.appendLine("${submargin}}")
                builder.appendLine("${margin}}")
            } else {
                if (unit.type == XmlUnitType.TAG) {
                    builder.appendLine("${margin}obj.${unit.propertyName}?.let { tag(\"${unit.xmlName}\", it) }")
                } else if (unit.type == XmlUnitType.ATTRIBUTE) {
                    builder.appendLine("${margin}obj.${unit.propertyName}?.let { attr(\"${unit.xmlName}\", it) }")
                }
            }
        } else {
            if (unit.children.isNotEmpty()) {
                if (unit.propertyName.isEmpty()) {
                    builder.appendLine("${margin}tag(\"${unit.xmlName}\") {")
                } else {
                    builder.appendLine("${margin}tag(\"${unit.xmlName}\", obj.${unit.propertyName}) {")
                }
                unit.children.renderChildren(builder, offset + 1)
                builder.appendLine("${margin}}")
            } else {
                if (unit.type == XmlUnitType.TAG) {
                    builder.appendLine("${margin}tag(\"${unit.xmlName}\", obj.${unit.propertyName})")
                } else if (unit.type == XmlUnitType.ATTRIBUTE) {
                    builder.appendLine("${margin}attr(\"${unit.xmlName}\", obj.${unit.propertyName})")
                }
            }
        }
    }
}