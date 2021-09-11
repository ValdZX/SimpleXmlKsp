package ua.vald_zx.simplexml.ksp.processor.generator

import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.Field

internal fun FunSpec.Builder.renderTag(
    serializerName: String?,
    argumentsFunArgument: String,
    field: Field.Tag,
    serializersMap: Map<Field, FieldSerializer>
) {
    if(field.children.isEmpty()) {
        if (field.isNullable) {
            tagNullableValue(serializerName, argumentsFunArgument, field)
        } else {
            tag(serializerName, argumentsFunArgument, field)
        }
    } else {
        if (field.isNullable) {
            tagNullableWithChildren(serializerName, argumentsFunArgument, field, serializersMap)
        } else {
            tagWithChildren(serializerName, argumentsFunArgument, field, serializersMap)
        }
    }
}

internal fun FunSpec.Builder.tag(
    serializerName: String?,
    argumentsFunArgument: String,
    field: Field.Tag
) {
    addStatement("${serializerName}.buildXml(this, \"${field.tagName}\", obj.${field.fieldName})$argumentsFunArgument")
}

internal fun FunSpec.Builder.tagNullableValue(
    serializerName: String?,
    argumentsFunArgument: String,
    field: Field.Tag
) {
    if (field.required) {
        addStatement("val ${field.fieldName} = obj.${field.fieldName}?: throw SerializeException(\"\"\"field ${field.tagName} value is required\"\"\")")
        addStatement("$serializerName.buildXml(this, \"${field.tagName}\", ${field.fieldName}$argumentsFunArgument)")
    } else {
        beginControlFlow("obj.${field.fieldName}?.let")
        addStatement("$serializerName.buildXml(this, \"${field.tagName}\", it$argumentsFunArgument)")
        endControlFlow()
    }
}

internal fun FunSpec.Builder.tagWithChildren(
    serializerName: String?,
    argumentsFunArgument: String,
    element: Field.Tag,
    serializersMap: Map<Field, FieldSerializer>
) {
    if (element.fieldName.isEmpty()) {
        beginControlFlow("tag(\"${element.tagName}\")")
    } else {
        beginControlFlow("$serializerName.buildXml(this, \"${element.tagName}\", obj.${element.fieldName}$argumentsFunArgument)")
    }
    renderChildren(element.children, serializersMap)
    endControlFlow()
}

internal fun FunSpec.Builder.tagNullableWithChildren(
    serializerName: String?,
    argumentsFunArgument: String,
    field: Field.Tag,
    serializersMap: Map<Field, FieldSerializer>
) {
    addStatement("val ${field.fieldName} = obj.${field.fieldName}")
    beginControlFlow("if (${field.fieldName} != null)")
    beginControlFlow("$serializerName.buildXml(this, \"${field.tagName}\", ${field.fieldName}$argumentsFunArgument)")
    renderChildren(field.children, serializersMap)
    endControlFlow()
    nextControlFlow("else")
    beginControlFlow("tag(\"${field.tagName}\")")
    renderChildren(field.children, serializersMap)
    endControlFlow()
    endControlFlow()
}