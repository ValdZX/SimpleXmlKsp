package ua.vald_zx.simplexml.ksp.processor.generator.element

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer

internal class AttributeGenerator(private val field: Field.Attribute) : ElementGenerator {

    private var serializerName: String? = null
    private lateinit var genericArguments: String
    private lateinit var serializersMap: Map<Field, FieldSerializer>

    override fun render(
        funBuilder: FunSpec.Builder,
        fieldSerializer: FieldSerializer?,
        serializersMap: Map<Field, FieldSerializer>
    ) {
        serializerName = fieldSerializer?.serializerVariableName
        val genericTypesVariableName = fieldSerializer?.genericTypesVariableName
        this.genericArguments = if (genericTypesVariableName != null) ", $genericTypesVariableName" else ""
        this.serializersMap = serializersMap

        if (field.isNullable) {
            funBuilder.nullableValue()
        } else {
            funBuilder.value()
        }
    }

    private fun FunSpec.Builder.value() {
        val serializeCall = "$serializerName.serialize(obj.${field.fieldName})"
        addStatement("attr(\"${field.attributeName}\", $serializeCall)")
    }

    private fun FunSpec.Builder.nullableValue() {
        val serializeCall = "$serializerName.serialize(it)"
        beginControlFlow("obj.${field.fieldName}?.let")
        addStatement("attr(\"${field.attributeName}\", $serializeCall)")
        endControlFlow()
    }
}