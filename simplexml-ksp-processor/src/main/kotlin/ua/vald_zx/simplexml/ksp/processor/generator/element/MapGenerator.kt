package ua.vald_zx.simplexml.ksp.processor.generator.element

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field
import ua.vald_zx.simplexml.ksp.processor.generator.FieldSerializer
import ua.vald_zx.simplexml.ksp.processor.generator.generateValues
import ua.vald_zx.simplexml.ksp.processor.generator.renderChildren

class MapGenerator(private val field: Field.Map) : ElementGenerator {

    private var fieldSerializer: FieldSerializer? = null
    private lateinit var serializersMap: Map<Field, FieldSerializer>

    override fun renderSerialization(
        funBuilder: FunSpec.Builder,
        fieldSerializer: FieldSerializer?,
        serializersMap: Map<Field, FieldSerializer>
    ) {
        this.fieldSerializer = fieldSerializer
        this.serializersMap = serializersMap
        funBuilder.map()
    }

    override fun renderDeserializationVariable(
        funBuilder: FunSpec.Builder,
        fieldToValueMap: MutableMap<String, String>,
        parentValueName: String,
        layer: Int,
        numberIterator: Iterator<Int>
    ) {
        val currentValueName = "layer${layer}Tag${numberIterator.next()}"
        if (field.isInline) {
            funBuilder.addStatement("val $currentValueName = $parentValueName?.getPairs(\"${field.keyName}\", \"${field.entryName}\")")
            fieldToValueMap[field.fieldName] = currentValueName
        } else {
            funBuilder.addStatement("val $currentValueName = $parentValueName?.get(\"${field.tagName}\")")
            val mapName = "layer${layer}Map${numberIterator.next()}"
            funBuilder.addStatement("val $mapName = $currentValueName?.getPairs(\"${field.keyName}\", \"${field.entryName}\")")
            fieldToValueMap[field.fieldName] = mapName
            funBuilder.generateValues(
                field.children.filterIsInstance<Field.Attribute>(),
                fieldToValueMap,
                currentValueName,
                layer + 1,
                numberIterator
            )
        }
    }

    private fun FunSpec.Builder.map() {
        if (field.isNullable) {
            mapNullableValue(fieldSerializer, field)
        } else {
            val keySerializerName = fieldSerializer?.serializerVariableName
            val keyGenericTypesVariableName = fieldSerializer?.genericTypesVariableName
            val valueSerializerVariableName = fieldSerializer?.valueSerializerVariableName
            val valueGenericTypesVariableName = fieldSerializer?.valueGenericTypesVariableName
            if (field.children.isNotEmpty()) {
                if (!field.isInline) {
                    beginControlFlow("tag(\"${field.tagName}\") {")
                    renderChildren(field.children, serializersMap)
                }
                printMapForeach(
                    field,
                    keySerializerName,
                    keyGenericTypesVariableName,
                    valueSerializerVariableName,
                    valueGenericTypesVariableName,
                    "obj.${field.fieldName}"
                )
                if (!field.isInline) {
                    endControlFlow()
                }
            } else {
                if (!field.isInline) {
                    beginControlFlow("tag(\"${field.tagName}\") {")
                }
                printMapForeach(
                    field,
                    keySerializerName,
                    keyGenericTypesVariableName,
                    valueSerializerVariableName,
                    valueGenericTypesVariableName,
                    "obj.${field.fieldName}"
                )
                if (!field.isInline) {
                    endControlFlow()
                }
            }
        }
    }

    private fun FunSpec.Builder.mapNullableValue(fieldSerializer: FieldSerializer?, element: Field.Map) {
        val keySerializerName = fieldSerializer?.serializerVariableName
        val keyGenericTypesVariableName = fieldSerializer?.genericTypesVariableName
        val valueSerializerVariableName = fieldSerializer?.valueSerializerVariableName
        val valueGenericTypesVariableName = fieldSerializer?.valueGenericTypesVariableName

        if (element.isInline) {
            beginControlFlow("obj.${element.fieldName}?.forEach")
            printMapForeach(
                element,
                keySerializerName,
                keyGenericTypesVariableName,
                valueSerializerVariableName,
                valueGenericTypesVariableName,
                "it"
            )
            endControlFlow()
        } else {
            beginControlFlow("obj.${element.fieldName}?.let")
            addStatement("map ->")
            beginControlFlow("tag(\"${element.tagName}\") {")
            printMapForeach(
                element,
                keySerializerName,
                keyGenericTypesVariableName,
                valueSerializerVariableName,
                valueGenericTypesVariableName,
                "map"
            )
            endControlFlow()
            endControlFlow()
        }
    }

    private fun FunSpec.Builder.printMapForeach(
        element: Field.Map,
        keySerializerName: String?,
        keyArgumentsFunArgument: String?,
        valueSerializerName: String?,
        valueArgumentsFunArgument: String?,
        fieldName: String
    ) {
        beginControlFlow("$fieldName.forEach")
        addStatement("(key, value) ->")
        addStatement("$keySerializerName.buildXml(this, \"${element.keyName}\", key${keyArgumentsFunArgument.orEmpty()})")
        addStatement("$valueSerializerName.buildXml(this, \"${element.entryName}\", value${valueArgumentsFunArgument.orEmpty()})")
        endControlFlow()
    }
}