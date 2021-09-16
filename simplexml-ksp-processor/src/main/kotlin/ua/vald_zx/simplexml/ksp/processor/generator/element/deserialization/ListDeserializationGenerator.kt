package ua.vald_zx.simplexml.ksp.processor.generator.element.deserialization

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.addDeserializeCallStatement
import ua.vald_zx.simplexml.ksp.processor.generator.generateFieldsValues

class ListDeserializationGenerator(private val field: Field.List) : ElementDeserializationGenerator {
    private val fieldName = field.fieldName
    private lateinit var valueName: String

    override fun renderDeserializationVariable(
        funBuilder: FunSpec.Builder,
        parentValueName: String,
        layer: Int,
        numberIterator: Iterator<Int>
    ) {
        val currentValueName = "layer${layer}Tag${numberIterator.next()}"
        if (field.isInline) {
            funBuilder.addStatement("val $currentValueName = $parentValueName?.getAll(\"${field.entryName}\")")
            valueName = currentValueName
        } else {
            funBuilder.addStatement("val $currentValueName = $parentValueName?.get(\"${field.tagName}\")")
            valueName = "layer${layer}List${numberIterator.next()}"
            funBuilder.addStatement("val $valueName = $currentValueName?.getAll(\"${field.entryName}\")")
            funBuilder.generateFieldsValues(
                field.children.filterIsInstance<Field.Attribute>(),
                currentValueName,
                layer + 1,
                numberIterator
            )
        }
    }

    override fun renderConstructorArgument(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer) {
        funBuilder.beginControlFlow("$fieldName = $valueName?.map")
        funBuilder.addDeserializeCallStatement("it", field, fieldSerializer, isNotNull = true)
        funBuilder.endControlFlow()
        if (field.isMutableCollection) {
            funBuilder.addStatement("?.toMutableList()")
        }
        funBuilder.addStatement("?: throw DeserializeException(\"\"\"${field.fieldType.parent.toString()} field $fieldName value is required\"\"\"),")
    }

    override fun renderFieldFilling(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer) {
        val entrySerializerName = fieldSerializer.serializerVariableName
        val genericTypesVariableName = fieldSerializer.genericTypesVariableName
        val argumentsFunArgument = if (genericTypesVariableName != null) {
            ", $genericTypesVariableName"
        } else ""
        if (!field.required) {
            funBuilder.beginControlFlow("if ($valueName != null)")
            funBuilder.beginControlFlow("$fieldName = $valueName.map")
            funBuilder.addStatement("$entrySerializerName.readData(it$argumentsFunArgument)")
            funBuilder.endControlFlow()
            if (field.isMutableCollection) {
                funBuilder.addStatement("?.toMutableList()")
            }
            funBuilder.endControlFlow()
        } else {
            funBuilder.beginControlFlow("$fieldName = $valueName?.map")
            funBuilder.addStatement("$entrySerializerName.readData(it$argumentsFunArgument)")
            funBuilder.endControlFlow()
            if (field.isMutableCollection) {
                funBuilder.addStatement("?.toMutableList()")
            }
            funBuilder.addStatement("?: throw DeserializeException(\"\"\"${field.fieldType.parent.toString()} field $fieldName value is required\"\"\")")
        }
    }
}