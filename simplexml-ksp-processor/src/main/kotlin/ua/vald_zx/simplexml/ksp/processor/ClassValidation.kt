package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.KSPLogger

fun ClassToGenerate.isValid(isStrictMode: Boolean, logger: KSPLogger): Boolean {
    fields.forEach {
        if (!it.isValid(isStrictMode, logger, this)) {
            return false
        }
    }
    return true
}

private fun Field.isValid(isStrictMode: Boolean, logger: KSPLogger, parent: ClassToGenerate): Boolean {
    val parentName = parent.fullName
    if (!isMutable && !isConstructorParameter) {
        val message = "$parentName::$fieldName. Change field to variable."
        if (isStrictMode) {
            error(message)
        } else {
            logger.warn(message)
        }
    }
    if (this is Field.List) {
        val typeSimpleName = fieldType.resolve().declaration.simpleName.asString()
        if (typeSimpleName != "List" && typeSimpleName != "MutableList") {
            val message =
                "$parentName failure. Illegal annotation on $fieldName. ElementList only for kotlin.collections.List"
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
