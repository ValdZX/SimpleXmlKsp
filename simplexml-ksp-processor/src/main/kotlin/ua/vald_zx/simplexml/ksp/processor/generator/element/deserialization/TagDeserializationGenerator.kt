package ua.vald_zx.simplexml.ksp.processor.generator.element.deserialization

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.addDeserializeCallStatement
import ua.vald_zx.simplexml.ksp.processor.generator.generateFieldsValues

internal class TagDeserializationGenerator(private val field: Field.Tag) : ElementDeserializationGenerator {

    private val fieldName = field.fieldName
    private lateinit var currentValueName: String

    override fun renderDeserializationVariable(
        funBuilder: FunSpec.Builder,
        parentValueName: String,
        layer: Int,
        numberIterator: Iterator<Int>
    ) {
        currentValueName = "layer${layer}Tag${numberIterator.next()}"
        funBuilder.addStatement("val $currentValueName = $parentValueName?.get(\"${field.tagName}\")")
        funBuilder.generateFieldsValues(
            field.children,
            currentValueName,
            layer + 1,
            numberIterator
        )
    }

    override fun renderConstructorArgument(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer) {
        funBuilder.addDeserializeCallStatement(
            prefix = "$fieldName = ",
            postfix = ",",
            fieldStatement = currentValueName,
            field = field,
            fieldSerializer = fieldSerializer
        )
    }

    override fun renderFieldFilling(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer) {
        if (!field.required) {
            funBuilder.beginControlFlow("if ($currentValueName != null)")
            funBuilder.addDeserializeCallStatement(currentValueName, field, fieldSerializer, "$fieldName = ", isNotNull = true)
            funBuilder.endControlFlow()
        } else {
            funBuilder.addDeserializeCallStatement(currentValueName, field, fieldSerializer, "$fieldName = ")
        }
    }
}