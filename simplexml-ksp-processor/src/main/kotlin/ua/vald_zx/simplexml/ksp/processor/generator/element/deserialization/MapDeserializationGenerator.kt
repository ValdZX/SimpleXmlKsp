package ua.vald_zx.simplexml.ksp.processor.generator.element.deserialization

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.generateFieldsValues

class MapDeserializationGenerator(private val field: Field.Map) : ElementDeserializationGenerator {

    val fieldName = field.fieldName

    private lateinit var valueName: String

    override fun renderDeserializationVariable(
        funBuilder: FunSpec.Builder,
        parentValueName: String,
        layer: Int,
        numberIterator: Iterator<Int>
    ) {
        val currentValueName = "layer${layer}Tag${numberIterator.next()}"
        if (field.isInline) {
            funBuilder.addStatement("val $currentValueName = $parentValueName?.getPairs(\"${field.keyName}\", \"${field.entryName}\")")
            valueName = currentValueName
        } else {
            funBuilder.addStatement("val $currentValueName = $parentValueName?.get(\"${field.tagName}\")")
            valueName = "layer${layer}Map${numberIterator.next()}"
            funBuilder.addStatement("val $valueName = $currentValueName?.getPairs(\"${field.keyName}\", \"${field.entryName}\")")
            funBuilder.generateFieldsValues(
                field.children.filterIsInstance<Field.Attribute>(),
                currentValueName,
                layer + 1,
                numberIterator
            )
        }
    }

    override fun renderConstructorArgument(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer) {
        val keySerializerName = fieldSerializer.serializerVariableName
        val valueSerializerName = fieldSerializer.valueSerializerVariableName
        val keyGenericTypesVariableName = fieldSerializer.genericTypesVariableName
        val keyArgumentsFunArgument = if (keyGenericTypesVariableName != null) {
            ", $keyGenericTypesVariableName"
        } else ""
        val valueGenericTypesVariableName = fieldSerializer.valueGenericTypesVariableName
        val valueArgumentsFunArgument = if (valueGenericTypesVariableName != null) {
            ", $valueGenericTypesVariableName"
        } else ""
        funBuilder.beginControlFlow("$fieldName = $valueName?.map")
        funBuilder.addStatement("(keyElement, valueElement) ->")
        funBuilder.addStatement("val keyData = $keySerializerName.readData(keyElement$keyArgumentsFunArgument)")
        funBuilder.addStatement("val valueData = $valueSerializerName.readData(valueElement$valueArgumentsFunArgument)")
        funBuilder.addStatement("keyData to valueData")
        funBuilder.endControlFlow()
        funBuilder.addStatement("?.toMap() ?: throw DeserializeException(\"\"\"${field.fieldType.parent.toString()} field $fieldName value is required\"\"\")")
        if (field.isMutableCollection) {
            funBuilder.addStatement("?.toMutableList()")
        }
        funBuilder.addStatement("?: throw DeserializeException(\"\"\"${field.fieldType.parent.toString()} field $fieldName value is required\"\"\"),")
    }

    override fun renderFieldFilling(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer) {
        val keySerializerName = fieldSerializer.serializerVariableName
        val valueSerializerName = fieldSerializer.valueSerializerVariableName
        val keyGenericTypesVariableName = fieldSerializer.genericTypesVariableName
        val keyArgumentsFunArgument = if (keyGenericTypesVariableName != null) {
            ", $keyGenericTypesVariableName"
        } else ""
        val valueGenericTypesVariableName = fieldSerializer.valueGenericTypesVariableName
        val valueArgumentsFunArgument = if (valueGenericTypesVariableName != null) {
            ", $valueGenericTypesVariableName"
        } else ""
        if (!field.required) {
            funBuilder.beginControlFlow("if ($valueName != null)")
            funBuilder.beginControlFlow("$fieldName = $valueName.map")
            funBuilder.addStatement("(keyElement, valueElement) ->")
            funBuilder.addStatement("val keyData = $keySerializerName.readData(keyElement$keyArgumentsFunArgument)")
            funBuilder.addStatement("val valueData = $valueSerializerName.readData(valueElement$valueArgumentsFunArgument)")
            funBuilder.addStatement("keyData to valueData")
            funBuilder.endControlFlow()
            funBuilder.addStatement(".toMap()")
            funBuilder.endControlFlow()
        } else {
            funBuilder.beginControlFlow("$fieldName = $valueName?.map")
            funBuilder.addStatement("(keyElement, valueElement) ->")
            funBuilder.addStatement("val keyData = $keySerializerName.readData(keyElement$keyArgumentsFunArgument)")
            funBuilder.addStatement("val valueData = $valueSerializerName.readData(valueElement$valueArgumentsFunArgument)")
            funBuilder.addStatement("keyData to valueData")
            funBuilder.endControlFlow()
            funBuilder.addStatement("?.toMap() ?: throw DeserializeException(\"\"\"${field.fieldType.parent.toString()} field $fieldName value is required\"\"\")")
        }
    }
}