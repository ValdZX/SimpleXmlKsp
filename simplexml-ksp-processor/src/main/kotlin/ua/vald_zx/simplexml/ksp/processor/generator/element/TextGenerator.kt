package ua.vald_zx.simplexml.ksp.processor.generator.element

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer

class TextGenerator(private val field: Field.Text) : ElementGenerator {
    override fun renderSerialization(
        funBuilder: FunSpec.Builder,
        fieldSerializer: FieldSerializer?,
        serializersMap: Map<Field, FieldSerializer>
    ) {
        TODO("Not yet implemented")
    }

    override fun renderDeserializationVariable(
        funBuilder: FunSpec.Builder,
        fieldToValueMap: MutableMap<String, String>,
        parentValueName: String,
        layer: Int,
        numberIterator: Iterator<Int>
    ) {
        TODO("Not yet implemented")
    }
}