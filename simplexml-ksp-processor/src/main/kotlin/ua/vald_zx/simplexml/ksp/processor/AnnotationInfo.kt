package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import ua.vald_zx.simplexml.ksp.*
import ua.vald_zx.simplexml.ksp.processor.XmlSymbolProcessor.Companion.LIBRARY_PACKAGE

data class AnnotationInfo(
    val inline: Boolean,
    val required: Boolean,
    val elementName: String,
    val entryListName: String,
    val keyMapName: String,
    val valueMapName: String,
    val path: String,
    val type: XmlUnitType,
    val converterType: KSType?,
    val propertyEntryType: KSTypeReference?,
    val propertyKeyType: KSTypeReference?,
    val propertyValueType: KSTypeReference?,
)

fun KSPropertyDeclaration.getAnnotationInfo(
    logger: KSPLogger,
    isStrictMode: Boolean,
    propertyType: KSTypeReference,
    parentName: String,
    propertyName: String,
): AnnotationInfo {
    return AnnotationIndoReader(
        logger,
        isStrictMode,
        propertyType,
        parentName,
        propertyName,
        annotations
    ).read()
}

private class AnnotationIndoReader(
    private val logger: KSPLogger,
    private val isStrictMode: Boolean,
    private val propertyType: KSTypeReference,
    private val parentName: String,
    private val propertyName: String,
    private val annotations: Sequence<KSAnnotation>,
) {
    var path = ""
    var entryListName = ""
    var keyMapName = ""
    var valueMapName = ""
    var elementName = ""
    var required = true
    var inline = false
    var converterType: KSType? = null
    var propertyEntryType: KSTypeReference? = null
    var propertyKeyType: KSTypeReference? = null
    var propertyValueType: KSTypeReference? = null
    var type: XmlUnitType = XmlUnitType.UNKNOWN

    fun read(): AnnotationInfo {
        annotations
            .filter { it.annotationType.resolve().declaration.packageName.asString() == LIBRARY_PACKAGE }
            .forEach { annotation ->
                when (annotation.shortName.asString()) {
                    Path::class.simpleName -> readPath(annotation)
                    Element::class.simpleName -> readElement(annotation)
                    Attribute::class.simpleName -> readAttribute(annotation)
                    ElementList::class.simpleName -> readList(annotation)
                    ElementMap::class.simpleName -> readMap(annotation)
                    Convert::class.simpleName -> {
                        converterType = annotation.arguments.first().value as KSType
                    }
                }
            }
        return AnnotationInfo(
            inline = inline,
            required = required,
            elementName = elementName,
            entryListName = entryListName,
            keyMapName = keyMapName,
            valueMapName = valueMapName,
            path = path,
            type = type,
            converterType = converterType,
            propertyEntryType = propertyEntryType,
            propertyKeyType = propertyKeyType,
            propertyValueType = propertyValueType,
        )
    }

    private fun readDataUnit(annotation: KSAnnotation) {
        checkType()
        annotation.arguments.forEach { arg ->
            when (arg.name?.getShortName()) {
                "name" -> {
                    elementName = arg.value.asString(default = propertyName)
                }
                "required" -> {
                    required = arg.value.asBoolean(default = true)
                }
            }
        }
    }

    private fun readElement(annotation: KSAnnotation) {
        readDataUnit(annotation)
        type = XmlUnitType.TAG
    }

    private fun readAttribute(annotation: KSAnnotation) {
        readDataUnit(annotation)
        type = XmlUnitType.ATTRIBUTE
    }

    private fun readList(annotation: KSAnnotation) {
        checkType()
        annotation.arguments.forEach { arg ->
            when (arg.name?.getShortName()) {
                "name" -> {
                    elementName = arg.value.asString(default = propertyName)
                }
                "entry" -> {
                    entryListName = arg.value.asString()
                }
                "required" -> {
                    required = arg.value.asBoolean(default = true)
                }
                "inline" -> {
                    inline = arg.value.asBoolean()
                }
            }
        }
        if (entryListName.isEmpty()) {
            entryListName = propertyType.getArgumentShortName(0)
        }
        type = XmlUnitType.LIST
        propertyEntryType = propertyType.element?.typeArguments?.first()?.type
    }

    private fun readMap(annotation: KSAnnotation) {
        checkType()
        annotation.arguments.forEach { arg ->
            when (arg.name?.getShortName()) {
                "name" -> {
                    elementName = arg.value.asString(default = propertyName)
                }
                "key" -> {
                    keyMapName = arg.value.asString()
                }
                "value" -> {
                    valueMapName = arg.value.asString()
                }
                "required" -> {
                    required = arg.value.asBoolean(default = true)
                }
                "inline" -> {
                    inline = arg.value.asBoolean()
                }
            }
        }
        if (keyMapName.isEmpty()) {
            keyMapName = propertyType.getArgumentShortName(0)
        }
        if (valueMapName.isEmpty()) {
            valueMapName = propertyType.getArgumentShortName(1)
        }
        type = XmlUnitType.MAP
        propertyKeyType = propertyType.element?.typeArguments?.get(0)?.type
        propertyValueType = propertyType.element?.typeArguments?.get(1)?.type
    }

    private fun checkType() {
        if (type != XmlUnitType.UNKNOWN) {
            if (isStrictMode) {
                error("$parentName failure. Illegal annotation on $propertyName")
            } else {
                logger.warn("Too many annotations. $parentName::$propertyName")
            }
        }
    }

    private fun readPath(annotation: KSAnnotation) {
        annotation.arguments.forEach { arg ->
            when (arg.name?.getShortName()) {
                "path" -> {
                    path = arg.value.asString()
                    if (path.isEmpty()) {
                        val message =
                            "$parentName::$propertyName failure. Path is empty, remove @Path or fill."
                        if (isStrictMode) {
                            error(message)
                        } else {
                            logger.warn("Path is ignored. $message")
                        }
                    }
                }
            }
        }
    }

    private fun Any?.asString(default: String = ""): String {
        return if (this is String && this.isNotEmpty()) this else default
    }

    private fun Any?.asBoolean(default: Boolean = false): Boolean {
        return (this as Boolean?) ?: default
    }

    private fun KSTypeReference.getArgumentShortName(index: Int): String {
        return element
            ?.typeArguments
            ?.get(index)
            ?.type
            ?.resolve()
            ?.declaration
            ?.simpleName
            ?.getShortName()
            ?: error("${parentName}:${propertyName}Invalid collection")
    }
}
