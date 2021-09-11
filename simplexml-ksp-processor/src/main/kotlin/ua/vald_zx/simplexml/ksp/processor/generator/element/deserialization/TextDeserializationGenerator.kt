package ua.vald_zx.simplexml.ksp.processor.generator.element.deserialization

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.addDeserializeCallStatement

class TextDeserializationGenerator(private val field: Field.Text) : ElementDeserializationGenerator {

    private val fieldName = field.fieldName
    private lateinit var currentValueName: String

    override fun renderDeserializationVariable(
        funBuilder: FunSpec.Builder,
        parentValueName: String,
        layer: Int,
        numberIterator: Iterator<Int>
    ) {
        currentValueName = parentValueName
    }

    override fun renderConstructorArgument(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer) {
        val serializerName = fieldSerializer.serializerVariableName
        val genericTypesVariableName = fieldSerializer.genericTypesVariableName
        val argumentsFunArgument = if (genericTypesVariableName != null) {
            ", $genericTypesVariableName"
        } else ""
        funBuilder.beginControlFlow("${field.fieldName} = if ($currentValueName?.text?.isEmpty() == true)")
        if (field.required) {
            funBuilder.addStatement("throw DeserializeException(\"\"\"field $fieldName value is required\"\"\")")
        } else {
            funBuilder.addStatement("null")
        }
        funBuilder.nextControlFlow("else")
        if (field.required) {
            funBuilder.addStatement("$serializerName.readData(")
            funBuilder.addStatement("$currentValueName ?: throw DeserializeException(\"\"\"field ${fieldName} value is required\"\"\")$argumentsFunArgument")
            funBuilder.addStatement(")")
        } else {
            funBuilder.addStatement("$currentValueName?.let { $serializerName.readData(it$argumentsFunArgument) }")
        }
        funBuilder.endControlFlow()
        funBuilder.addStatement(",")
    }

    override fun renderFieldFilling(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer) {
        val serializerName = fieldSerializer.serializerVariableName
        val genericTypesVariableName = fieldSerializer.genericTypesVariableName
        val argumentsFunArgument = if (genericTypesVariableName != null) {
            ", $genericTypesVariableName"
        } else ""
        funBuilder.beginControlFlow("${field.fieldName} = if ($currentValueName?.text?.isEmpty() == true)")
        if (field.required) {
            funBuilder.addStatement("throw DeserializeException(\"\"\"field $fieldName value is required\"\"\")")
        } else {
            funBuilder.addStatement("null")
        }
        funBuilder.nextControlFlow("else")
        if (!field.required) {
            funBuilder.addStatement("$serializerName.readData($currentValueName$argumentsFunArgument)")
        } else {
            funBuilder.addStatement("$serializerName.readData(")
            funBuilder.addStatement("$currentValueName ?: throw DeserializeException(\"\"\"field ${field.fieldName} value is required\"\"\")$argumentsFunArgument")
            funBuilder.addStatement(")")
        }
        funBuilder.endControlFlow()
    }
}