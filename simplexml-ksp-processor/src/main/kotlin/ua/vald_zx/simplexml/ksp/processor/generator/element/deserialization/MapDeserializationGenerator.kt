package ua.vald_zx.simplexml.ksp.processor.generator.element.deserialization

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.element.deserialization.MapDeserializationGenerator.MapFieldSerializerName.Companion.toSerName
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
        if (field.isAttribute && field.isInline) {
            funBuilder.addStatement("val $currentValueName = $parentValueName?.getAll(\"${field.entryName}\")")
            valueName = currentValueName
        } else if (field.isAttribute) {
            funBuilder.addStatement("val $currentValueName = $parentValueName?.get(\"${field.tagName}\")")
            valueName = "layer${layer}Map${numberIterator.next()}"
            funBuilder.addStatement("val $valueName = $currentValueName?.getAll(\"${field.entryName}\")")
            funBuilder.generateFieldsValues(
                field.children.filterIsInstance<Field.Attribute>(),
                currentValueName,
                layer + 1,
                numberIterator
            )
        } else if (field.isInline) {
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

    private data class MapFieldSerializerName(
        val keyName: String,
        val valueName: String?,
        val keyArgs: String,
        val valueArgs: String,
    ) {
        companion object {
            fun FieldSerializer.toSerName(): MapFieldSerializerName {
                val keySerializerName = firstValSerializer.serializerVariableName
                val valueSerializerName = secondValSerializer?.serializerVariableName
                val keyGenericTypesVariableName = firstValSerializer.genericTypesVariableName
                val keyArgumentsFunArgument = if (keyGenericTypesVariableName != null) {
                    ", $keyGenericTypesVariableName"
                } else ""
                val valueGenericTypesVariableName = secondValSerializer?.genericTypesVariableName
                val valueArgumentsFunArgument = if (valueGenericTypesVariableName != null) {
                    ", $valueGenericTypesVariableName"
                } else ""
                return MapFieldSerializerName(
                    keySerializerName,
                    valueSerializerName,
                    keyArgumentsFunArgument,
                    valueArgumentsFunArgument
                )
            }
        }
    }

    override fun renderConstructorArgument(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer) {
        val serName = fieldSerializer.toSerName()
        if (field.isAttribute) {
            funBuilder.generateReadingArgument(serName, true)
            if (field.isMutableCollection) {
                funBuilder.addStatement("?.toMutableMap()")
            }
            funBuilder.addStatement("?: throw DeserializeException(\"\"\"${field.fieldType.parent.toString()} field $fieldName value is required\"\"\"),")
        } else {
            funBuilder.generateReading(serName, true)
            if (field.isMutableCollection) {
                funBuilder.addStatement("?.toMutableMap()")
            }
            funBuilder.addStatement("?: throw DeserializeException(\"\"\"${field.fieldType.parent.toString()} field $fieldName value is required\"\"\"),")
        }
    }

    override fun renderFieldFilling(funBuilder: FunSpec.Builder, fieldSerializer: FieldSerializer) {
        val serName = fieldSerializer.toSerName()
        if (field.isAttribute) {
            if (!field.required) {
                funBuilder.beginControlFlow("if ($valueName != null)")
                funBuilder.generateReadingArgument(serName, false)
                funBuilder.endControlFlow()
            } else {
                funBuilder.generateReadingArgument(serName, true)
                funBuilder.addStatement("?: throw DeserializeException(\"\"\"${field.fieldType.parent.toString()} field $fieldName value is required\"\"\")")
            }
        } else {
            if (!field.required) {
                funBuilder.beginControlFlow("if ($valueName != null)")
                funBuilder.generateReading(serName, false)
                funBuilder.endControlFlow()
            } else {
                funBuilder.generateReading(serName, true)
                funBuilder.addStatement("?: throw DeserializeException(\"\"\"${field.fieldType.parent.toString()} field $fieldName value is required\"\"\")")
            }
        }
    }

    private fun FunSpec.Builder.generateReading(serName: MapFieldSerializerName, valueIsNullable: Boolean) {
        if (valueIsNullable) {
            beginControlFlow("$fieldName = $valueName?.map")
        } else {
            beginControlFlow("$fieldName = $valueName.map")
        }
        addStatement("(keyElement, valueElement) ->")
        addStatement("val keyData = ${serName.keyName}.readData(keyElement${serName.keyArgs})")
        addStatement("val valueData = ${serName.valueName}.readData(valueElement${serName.valueArgs})")
        addStatement("keyData to valueData")
        endControlFlow()
        if (valueIsNullable) {
            addStatement("?.toMap()")
        } else {
            addStatement(".toMap()")
        }
    }

    private fun FunSpec.Builder.generateReadingArgument(serName: MapFieldSerializerName, valueIsNullable: Boolean) {
        if (valueIsNullable) {
            beginControlFlow("$fieldName = $valueName?.associate")
        } else {
            beginControlFlow("$fieldName = $valueName.associate")
        }
        addStatement("val keyAttribute = it.attribute(\"${field.keyName}\")")
        addStatement("val keyData = ${serName.keyName}.readData(keyAttribute${serName.keyArgs})")
        addStatement("val valueData = ${serName.valueName}.readData(it${serName.valueArgs})")
        addStatement("keyData to valueData")
        endControlFlow()
    }
}