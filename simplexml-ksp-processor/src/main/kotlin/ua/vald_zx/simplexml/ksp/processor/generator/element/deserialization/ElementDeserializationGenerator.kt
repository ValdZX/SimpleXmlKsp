package ua.vald_zx.simplexml.ksp.processor.generator.element.deserialization

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer

interface ElementDeserializationGenerator {

    fun renderDeserializationVariable(
        funBuilder: FunSpec.Builder,
        parentValueName: String,
        layer: Int,
        numberIterator: Iterator<Int>
    )

    fun renderConstructorArgument(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer)

    fun renderFieldFilling(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer)
}