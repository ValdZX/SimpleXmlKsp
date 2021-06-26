package ua.vald_zx.simplexml.ksp.processor.generator

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.FieldElement
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

private fun Iterable<FieldElement>.renderChildren(builder: StringBuilder, offset: Int) {
    val margin = " ".repeat(offset * 4)
    forEach { field ->
        if (field.children.isNotEmpty()) {
            if (field.propertyName.isEmpty()) {
                builder.appendLine("${margin}tag(\"${field.name}\") {")
            } else {
                builder.appendLine("${margin}tag(\"${field.name}\", obj.${field.propertyName}) {")
            }
            field.children.renderChildren(builder, offset + 1)
            builder.appendLine("${margin}}")
        } else {
            if (field.type == XmlUnitType.TAG) {
                builder.appendLine("${margin}tag(\"${field.name}\", obj.${field.propertyName})")
            } else if (field.type == XmlUnitType.ATTRIBUTE) {
                builder.appendLine("${margin}attr(\"${field.name}\", obj.${field.propertyName})")
            }
        }
    }
}