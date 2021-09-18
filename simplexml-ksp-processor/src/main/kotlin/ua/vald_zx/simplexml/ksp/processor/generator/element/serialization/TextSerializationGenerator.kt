package ua.vald_zx.simplexml.ksp.processor.generator.element.serialization

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer

class TextSerializationGenerator(private val field: Field.Text) : ElementSerializationGenerator {
    private var serializerName: String? = null
    private lateinit var genericArguments: String

    override fun render(
        funBuilder: FunSpec.Builder,
        fieldSerializer: FieldSerializer?,
        serializersMap: Map<Field, FieldSerializer>
    ) {
        serializerName = fieldSerializer?.firstValSerializer?.serializerVariableName
        val genericTypesVariableName = fieldSerializer?.firstValSerializer?.genericTypesVariableName
        this.genericArguments = if (genericTypesVariableName != null) ", $genericTypesVariableName" else ""
        if (field.isNullable) {
            funBuilder.nullableValue()
        } else {
            funBuilder.value()
        }
    }

    private fun FunSpec.Builder.value() {
        val serializeCall = "$serializerName.serialize(obj.${field.fieldName})"
        addStatement("text($serializeCall)")
    }

    private fun FunSpec.Builder.nullableValue() {
        val serializeCall = "$serializerName.serialize(it)"
        beginControlFlow("obj.${field.fieldName}?.let")
        addStatement("text($serializeCall)")
        endControlFlow()
    }
}