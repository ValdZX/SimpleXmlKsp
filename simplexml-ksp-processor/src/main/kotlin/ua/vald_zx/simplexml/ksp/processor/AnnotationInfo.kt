package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.KSPLogger
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
    val path: String,
    val type: XmlUnitType,
    val converterType: KSType?,
    val propertyEntryType: KSTypeReference?,
)

fun KSPropertyDeclaration.getAnnotationInfo(
    logger: KSPLogger,
    isStrictMode: Boolean,
    propertyType: KSTypeReference,
    parentName: String,
    propertyName: String,
): AnnotationInfo {
    var path = ""
    var entryListName = ""
    var elementName = ""
    var required = true
    var inline = false
    var converterType: KSType? = null
    var propertyEntryType: KSTypeReference? = null
    var type: XmlUnitType = XmlUnitType.UNKNOWN
    annotations
        .filter { it.annotationType.resolve().declaration.packageName.asString() == LIBRARY_PACKAGE }
        .forEach { annotation ->
        when (val annotationName = annotation.shortName.asString()) {
            Path::class.simpleName -> {
                annotation.arguments.forEach { arg ->
                    when (arg.name?.getShortName()) {
                        "path" -> {
                            path = arg.value.asString()
                            if (path.isEmpty()) {
                                val message = "$parentName::$propertyName failure. Path is empty, remove @Path or fill."
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
            Element::class.simpleName,
            Attribute::class.simpleName -> {
                if (type != XmlUnitType.UNKNOWN) {
                    if (isStrictMode) {
                        error("$parentName failure. Illegal annotation on $propertyName")
                    } else {
                        logger.warn("Too many annotations. $parentName::$propertyName")
                    }
                }
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
                if (annotationName == Element::class.simpleName) {
                    type = XmlUnitType.TAG
                } else if (annotationName == Attribute::class.simpleName) {
                    type = XmlUnitType.ATTRIBUTE
                }
            }
            ElementList::class.simpleName -> {
                if (type != XmlUnitType.UNKNOWN) {
                    if (isStrictMode) {
                        error("$parentName failure. Illegal annotation on $propertyName")
                    } else {
                        logger.warn("Too many annotations. $parentName::$propertyName")
                    }
                }
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
                    entryListName = propertyType
                        .element
                        ?.typeArguments
                        ?.first()
                        ?.type
                        ?.resolve()
                        ?.declaration
                        ?.simpleName
                        ?.getShortName()
                        ?: error("$parentName::$propertyName invalid list")
                }
                type = XmlUnitType.LIST
                propertyEntryType = propertyType.element?.typeArguments?.first()?.type
            }
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
        path = path,
        type = type,
        converterType = converterType,
        propertyEntryType = propertyEntryType
    )
}


private fun Any?.asString(default: String = ""): String {
    return if (this is String && this.isNotEmpty()) this else default
}

private fun Any?.asBoolean(default: Boolean = false): Boolean {
    return (this as Boolean?) ?: default
}