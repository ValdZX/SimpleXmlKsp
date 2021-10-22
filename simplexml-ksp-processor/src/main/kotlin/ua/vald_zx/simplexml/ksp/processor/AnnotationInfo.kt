package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import ua.vald_zx.simplexml.ksp.*
import ua.vald_zx.simplexml.ksp.processor.XmlSymbolProcessor.Companion.LIBRARY_PACKAGE

private enum class XmlUnitType {
    TEXT,
    TAG,
    LIST,
    MAP,
    ATTRIBUTE,
    UNKNOWN
}

class AnnotationInfoReader(
    private val property: KSPropertyDeclaration,
    private val parent: KSClassDeclaration,
    private val logger: KSPLogger,
    private val isStrictMode: Boolean,
) {
    private val propertyName = property.simpleName.asString()
    private val propertyType = property.type
    private val parentName = parent.fullName
    private var path = ""
    private var entryName = ""
    private var keyName = ""
    private var isAttribute = false
    private var elementName = ""
    private var required = true
    private var isInline = false
    private var converterType: KSType? = null
    private var entryType: KSTypeReference? = null
    private var keyType: KSTypeReference? = null
    private var xmlUnitType: XmlUnitType = XmlUnitType.UNKNOWN

    fun read(): Field {
        property.annotations
            .filter { it.annotationType.resolve().declaration.packageName.asString() == LIBRARY_PACKAGE }
            .forEach { annotation ->
                when (annotation.shortName.asString()) {
                    Element::class.simpleName -> readElement(annotation)
                    Attribute::class.simpleName -> readAttribute(annotation)
                    Path::class.simpleName -> readPath(annotation)
                    Text::class.simpleName -> readText(annotation)
                    ElementList::class.simpleName -> readList(annotation)
                    ElementMap::class.simpleName -> readMap(annotation)
                    Convert::class.simpleName -> {
                        converterType = annotation.arguments.first().value as KSType
                    }
                }
            }
        val constructorParameters = parent.primaryConstructor?.parameters
        val constructorParameter = constructorParameters?.find { it.name?.asString() == propertyName }
        val isVariable = property.isMutable
        val isNullable = property.type.resolve().nullability != Nullability.NOT_NULL
        val isMutableCollection = property.type.isMutableCollection()
        val isConstructorParameter = constructorParameter != null
        val hasDefaultValue = constructorParameter?.hasDefault ?: false
        val requiredToConstructor = constructorParameter?.hasDefault?.not() ?: false
        if (requiredToConstructor && !required && !isNullable) {
            error("$parentName::$propertyName is not required without default value")
        }

        return when (xmlUnitType) {
            XmlUnitType.TAG -> Field.Tag(
                hasDefaultValue = hasDefaultValue,
                isNullable = isNullable,
                isMutable = isVariable,
                isConstructorParameter = isConstructorParameter,
                path = path,
                converterType = converterType,
                required = required,
                tagName = elementName,
                fieldName = propertyName,
                fieldType = propertyType
            )
            XmlUnitType.ATTRIBUTE -> Field.Attribute(
                hasDefaultValue = hasDefaultValue,
                isNullable = isNullable,
                isMutable = isVariable,
                isConstructorParameter = isConstructorParameter,
                path = path,
                converterType = converterType,
                required = required,
                attributeName = elementName,
                fieldName = propertyName,
                fieldType = propertyType
            )
            XmlUnitType.TEXT -> Field.Text(
                hasDefaultValue = hasDefaultValue,
                isNullable = isNullable,
                isMutable = isVariable,
                isConstructorParameter = isConstructorParameter,
                converterType = converterType,
                required = required,
                fieldName = propertyName,
                fieldType = propertyType,
            )
            XmlUnitType.LIST -> Field.List(
                hasDefaultValue = hasDefaultValue,
                isNullable = isNullable,
                isMutable = isVariable,
                isConstructorParameter = isConstructorParameter,
                path = path,
                fieldName = propertyName,
                fieldType = propertyType,
                converterType = converterType,
                required = required,
                tagName = elementName,
                isInline = isInline,
                isMutableCollection = isMutableCollection,
                entryName = entryName,
                entryType = entryType!!,
            )
            XmlUnitType.MAP -> Field.Map(
                hasDefaultValue = hasDefaultValue,
                isNullable = isNullable,
                isMutable = isVariable,
                isConstructorParameter = isConstructorParameter,
                path = path,
                fieldName = propertyName,
                fieldType = propertyType,
                converterType = converterType,
                required = required,
                tagName = elementName,
                isAttribute = isAttribute,
                isInline = isInline,
                isMutableCollection = isMutableCollection,
                keyName = keyName,
                keyType = keyType!!,
                entryName = entryName,
                entryType = entryType!!,
            )
            XmlUnitType.UNKNOWN -> TODO()
        }
    }

    private fun KSTypeReference.isMutableCollection(): Boolean {
        val typeSimpleName = resolve().declaration.simpleName.asString()
        return typeSimpleName == "MutableList" || typeSimpleName == "MutableMap"
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
        xmlUnitType = XmlUnitType.TAG
    }

    private fun readAttribute(annotation: KSAnnotation) {
        readDataUnit(annotation)
        xmlUnitType = XmlUnitType.ATTRIBUTE
    }

    private fun readText(annotation: KSAnnotation) {
        checkType()
        annotation.arguments.forEach { arg ->
            if (arg.name?.getShortName() == "required") {
                required = arg.value.asBoolean(default = true)
            }
        }
        xmlUnitType = XmlUnitType.TEXT
    }

    private fun readList(annotation: KSAnnotation) {
        checkType()
        annotation.arguments.forEach { arg ->
            when (arg.name?.getShortName()) {
                "name" -> {
                    elementName = arg.value.asString(default = propertyName)
                }
                "entry" -> {
                    entryName = arg.value.asString()
                }
                "required" -> {
                    required = arg.value.asBoolean(default = true)
                }
                "inline" -> {
                    isInline = arg.value.asBoolean()
                }
            }
        }
        if (entryName.isEmpty()) {
            entryName = propertyType.getArgumentShortName(0)
        }
        xmlUnitType = XmlUnitType.LIST
        entryType = propertyType.element?.typeArguments?.first()?.type
    }

    private fun readMap(annotation: KSAnnotation) {
        checkType()
        annotation.arguments.forEach { arg ->
            when (arg.name?.getShortName()) {
                "name" -> {
                    elementName = arg.value.asString(default = propertyName)
                }
                "key" -> {
                    keyName = arg.value.asString()
                }
                "entry" -> {
                    entryName = arg.value.asString()
                }
                "attribute" -> {
                    isAttribute = arg.value.asBoolean(default = false)
                }
                "required" -> {
                    required = arg.value.asBoolean(default = true)
                }
                "inline" -> {
                    isInline = arg.value.asBoolean()
                }
            }
        }
        if (keyName.isEmpty()) {
            keyName = propertyType.getArgumentShortName(0)
        }
        if (entryName.isEmpty()) {
            entryName = propertyType.getArgumentShortName(1)
        }
        xmlUnitType = XmlUnitType.MAP
        keyType = propertyType.element?.typeArguments?.get(0)?.type
        entryType = propertyType.element?.typeArguments?.get(1)?.type
    }

    private fun checkType() {
        if (xmlUnitType != XmlUnitType.UNKNOWN) {
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
