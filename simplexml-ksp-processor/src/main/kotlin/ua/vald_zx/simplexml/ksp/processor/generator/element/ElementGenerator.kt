package ua.vald_zx.simplexml.ksp.processor.generator.element

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer

interface ElementGenerator {
    fun renderSerialization(
        funBuilder: FunSpec.Builder,
        fieldSerializer: FieldSerializer?,
        serializersMap: Map<Field, FieldSerializer>
    )

    fun renderDeserializationVariable(
        funBuilder: FunSpec.Builder,
        fieldToValueMap: MutableMap<String, String>,
        parentValueName: String,
        layer: Int,
        numberIterator: Iterator<Int>
    )
}