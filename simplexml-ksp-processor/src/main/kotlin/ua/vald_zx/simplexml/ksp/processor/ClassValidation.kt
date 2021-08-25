package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.KSPLogger

fun ClassToGenerate.isValid(isStrictMode: Boolean, logger: KSPLogger): Boolean {
    propertyElements.forEach {
        if (!it.isValid(isStrictMode, logger, this)) {
            return false
        }
    }
    return true
}

private fun PropertyElement.isValid(isStrictMode: Boolean, logger: KSPLogger, parent: ClassToGenerate): Boolean {
    val parentName = parent.fullName
    if (!isVariable && !isConstructorParameter) {
        val message = "$parentName::$propertyName. Change field to variable."
        if (isStrictMode) {
            error(message)
        } else {
            logger.warn(message)
        }
    }
    if (xmlType == XmlUnitType.LIST) {
        val typeSimpleName = propertyType.resolve().declaration.simpleName.asString()
        if (typeSimpleName != "List" && typeSimpleName != "MutableList") {
            val message =
                "$parentName failure. Illegal annotation on $propertyName. ElementList only for kotlin.collections.List"
            if (isStrictMode) {
                error(message)
            } else {
                logger.warn(message)
            }
            return false
        }
    }
    return true
}
