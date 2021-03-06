package ua.vald_zx.simplexml.ksp.processor.generator

import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.squareup.kotlinpoet.FunSpec
import ua.vald_zx.simplexml.ksp.processor.ClassToGenerate
import ua.vald_zx.simplexml.ksp.processor.Field

internal fun FunSpec.Builder.generateDeserialization(
    classToGenerate: ClassToGenerate
): FunSpec.Builder {
    val serializersMap = generateAndGetSerializers(classToGenerate)
    addStatement("element as TagXmlElement?")
    generateFieldsValues(classToGenerate.dom, "element", 0, sequence {
        var counter = 0
        while (true) {
            yield(counter)
            counter++
        }
    }.iterator())

    val anyGenerics = if (classToGenerate.typeParameters.isNotEmpty()) {
        buildString {
            append("<")
            append(classToGenerate.typeParameters.joinToString(", ") { "Any" })
            append(">")
        }
    } else ""
    addStatement("return ${classToGenerate.name}$anyGenerics(")
    val propertyElements = classToGenerate.fields
    val (propertiesRequiredToConstructor, propertiesDynamics) = propertyElements
        .partition { it.isConstructorParameter && !it.hasDefaultValue }
    propertiesRequiredToConstructor.forEach { field ->
        val fieldSerializer = serializersMap[field] ?: error("Not found serializer")
        field.deserializationGenerator.renderConstructorArgument(this, fieldSerializer)
    }
    if (propertiesDynamics.isEmpty()) {
        addStatement(")")
    } else {
        beginControlFlow(").apply")
        propertiesDynamics.forEach { field ->
            val fieldSerializer = serializersMap[field] ?: error("Not found serializer")
            field.deserializationGenerator.renderFieldFilling(this, fieldSerializer)
        }
        endControlFlow()
    }
    return this
}

internal fun FunSpec.Builder.generateFieldsValues(
    dom: List<Field>,
    parentValueName: String,
    layer: Int,
    numberIterator: Iterator<Int>
) {
    dom.forEach { field ->
        field.deserializationGenerator.renderDeserializationVariable(
            this,
            parentValueName,
            layer,
            numberIterator
        )
    }
}

internal fun FunSpec.Builder.addDeserializeCallStatement(
    fieldStatement: String,
    field: Field,
    fieldSerializer: FieldSerializer,
    prefix: String = "",
    postfix: String = "",
    isNotNull: Boolean = false
) {
    val valSerializer = fieldSerializer.firstValSerializer
    val serializerName = valSerializer.serializerVariableName
    val genericTypesVariableName = valSerializer.genericTypesVariableName
    val argumentsFunArgument = if (genericTypesVariableName != null) {
        ", $genericTypesVariableName"
    } else ""

    if (field.required && !isNotNull) {
        addStatement("$prefix$serializerName.readData(")
        addStatement("$fieldStatement ?: throw DeserializeException(\"\"\"field ${field.fieldName} value is required\"\"\")$argumentsFunArgument")
        val arguments = field.fieldType?.resolve()?.arguments
        if (arguments?.isNotEmpty() == true) {
            val anyArgs = arguments.joinToString { typeArgument ->
                typeArgument.toStringWithArgs()
            }
            addStatement(") as ${field.fieldType.toString()}<$anyArgs>$postfix")
        } else {
            addStatement(")$postfix")
        }
    } else if (isNotNull) {
        addStatement("$prefix$serializerName.readData($fieldStatement$argumentsFunArgument)$postfix")
    } else {
        addStatement("$prefix$fieldStatement?.let { $serializerName.readData(it$argumentsFunArgument) }$postfix")
    }
}

private fun KSTypeArgument.toStringWithArgs(): String {
    val type = this.type ?: return "Any"
    val ksType = type.resolve()
    val arguments = ksType.arguments
    return if (ksType.declaration is KSTypeParameter) {
        "Any"
    } else if (arguments.isEmpty()) {
        type.toString()
    } else {
        val ksTypeArguments = arguments.joinToString { it.toStringWithArgs() }
        ksType.declaration.toString() + "<$ksTypeArguments>"
    }
}