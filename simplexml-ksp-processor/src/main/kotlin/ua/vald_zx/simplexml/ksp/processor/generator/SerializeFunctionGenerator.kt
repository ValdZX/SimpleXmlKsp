package ua.vald_zx.simplexml.ksp.processor.generator

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.Field


internal fun FunSpec.Builder.generateSerialization(classToGenerate: ClassToGenerate): FunSpec.Builder {
    val serializersMap = generateAndGetSerializers(classToGenerate)
    beginControlFlow("tagFather.apply")
    renderChildren(classToGenerate.dom, serializersMap)
    endControlFlow()
    return this
}

internal fun FunSpec.Builder.renderChildren(
    fields: Iterable<Field>,
    serializersMap: Map<Field, FieldSerializer>
) {
    fields.forEach { field ->
        val fieldSerializer = serializersMap.toMutableMap()[field]
        val serializerName = fieldSerializer?.serializerVariableName
        val genericTypesVariableName = fieldSerializer?.genericTypesVariableName
        val argumentsFunArgument = if (genericTypesVariableName != null) {
            ", $genericTypesVariableName"
        } else ""
        when (field) {
            is Field.Tag -> renderTag(serializerName, argumentsFunArgument, field, serializersMap)
            is Field.Attribute -> attribute(serializerName, field)
            is Field.List -> list(serializerName, argumentsFunArgument, field, serializersMap)
            is Field.Map -> map(fieldSerializer, field, serializersMap)
            else -> error("Not supported XmlUnitType $field")
        }
    }
}

private fun FunSpec.Builder.printListForeach(
    element: Field.List,
    entrySerializerName: String?,
    argumentsFunArgument: String
) {
    beginControlFlow("obj.${element.fieldName}.forEach")
    addStatement("$entrySerializerName.buildXml(this, \"${element.entryName}\", it$argumentsFunArgument)")
    endControlFlow()
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

private fun FunSpec.Builder.attribute(serializerName: String?, field: Field.Attribute) {
    if (field.isNullable) {
        attributeNullableValue(serializerName, field)
    } else {
        val serializeCall = "$serializerName.serialize(obj.${field.fieldName})"
        addStatement("attr(\"${field.attributeName}\", $serializeCall)")
    }
}

private fun FunSpec.Builder.attributeNullableValue(serializerName: String?, element: Field.Attribute) {
    val serializeCall = "$serializerName.serialize(it)"
    beginControlFlow("obj.${element.fieldName}?.let")
    addStatement("attr(\"${element.attributeName}\", $serializeCall)")
    endControlFlow()
}

private fun FunSpec.Builder.list(
    serializerName: String?,
    argumentsFunArgument: String,
    field: Field.List,
    serializersMap: Map<Field, FieldSerializer>
) {
    if (field.isNullable) {
        listNullableValue(serializerName, argumentsFunArgument, field)
    } else {
        if (field.children.isNotEmpty()) {
            if (field.isInline) {
                printListForeach(field, serializerName, argumentsFunArgument)
            } else {
                beginControlFlow("tag(\"${field.tagName}\") {")
                renderChildren(field.children, serializersMap)
                printListForeach(field, serializerName, argumentsFunArgument)
                endControlFlow()
            }
        } else {
            if (field.isInline) {
                printListForeach(field, serializerName, argumentsFunArgument)
            } else {
                beginControlFlow("tag(\"${field.tagName}\") {")
                printListForeach(field, serializerName, argumentsFunArgument)
                endControlFlow()
            }
        }
    }
}

private fun FunSpec.Builder.map(
    fieldSerializer: FieldSerializer?,
    field: Field.Map,
    serializersMap: Map<Field, FieldSerializer>
) {
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

private fun FunSpec.Builder.listNullableValue(
    serializerName: String?,
    argumentsFunArgument: String,
    element: Field.List
) {
    if (element.isInline) {
        beginControlFlow("obj.${element.fieldName}?.forEach")
        addStatement("$serializerName.buildXml(this, \"${element.entryName}\", it$argumentsFunArgument)")
        endControlFlow()
    } else {
        beginControlFlow("obj.${element.fieldName}?.let")
        addStatement("list ->")
        beginControlFlow("tag(\"${element.tagName}\") {")
        beginControlFlow("list.forEach {")
        addStatement("$serializerName.buildXml(this, \"${element.entryName}\", it$argumentsFunArgument)")
        endControlFlow()
        endControlFlow()
        endControlFlow()
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