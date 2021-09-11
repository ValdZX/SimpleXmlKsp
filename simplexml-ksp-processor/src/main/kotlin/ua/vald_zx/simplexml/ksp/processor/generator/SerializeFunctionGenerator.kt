package ua.vald_zx.simplexml.ksp.processor.generator

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.Field


internal fun FunSpec.Builder.generateSerialization(classToGenerate: ClassToGenerate): FunSpec.Builder {
    val serializersMap = generateAndGetSerializers(classToGenerate)
    beginControlFlow("tagFather.apply")
    renderChildren(classToGenerate.dom, serializersMap)
    endControlFlow()
    return this
}

internal fun FunSpec.Builder.renderChildren(
    fields: Iterable<Field>,
    serializersMap: Map<Field, FieldSerializer>
) {
    fields.forEach { field ->
        val fieldSerializer = serializersMap.toMutableMap()[field]
        field.serializationGenerator.render(this, fieldSerializer, serializersMap)
    }
}