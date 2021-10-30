package ua.vald_zx.simplexml.ksp.processor

import com.google.devtools.ksp.processing.KSPLogger

class ClassValidator(
    private val classToGenerate: ClassToGenerate,
    private val isStrictMode: Boolean,
    private val logger: KSPLogger
) {
    fun isValid(): Boolean {
        classToGenerate.fields.forEach {
            if (!it.isValid()) {
                return false
            }
        }
        return validateTagCollision(classToGenerate.dom)
    }

    private fun validateTagCollision(layer: List<Field>): Boolean {
        val duplicatedTags = layer
            .mapNotNull { it.getTagNameOrNull() }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .map { it.key }
        if (duplicatedTags.isNotEmpty()) {
            val duplicatesString = duplicatedTags.joinToString()
            val message = "${classToGenerate.bean.logDirection} has duplicated tags: $duplicatesString"
            if (isStrictMode) {
                error(message)
            } else {
                logger.warn(message)
            }
            return false
        }
        val duplicatedAttrs = layer
            .filterIsInstance<Field.Attribute>()
            .map { it.attributeName }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .map { it.key }
        if (duplicatedAttrs.isNotEmpty()) {
            val duplicatesString = duplicatedAttrs.joinToString()
            val message = "${classToGenerate.bean.logDirection} has duplicated attributes: $duplicatesString"
            if (isStrictMode) {
                error(message)
            } else {
                logger.warn(message)
            }
            return false
        }
        layer.forEach { field ->
            val children = field.getChildrenOrNull()
            if (children != null && !validateTagCollision(children)) {
                return false
            }
        }
        return true
    }

    private fun Field.isValid(): Boolean {
        if (!isMutable && !isConstructorParameter) {
            val message = "${logDirection(classToGenerate.bean)}. Change field to variable."
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
                    "${logDirection(classToGenerate.bean)}. Illegal annotation. ElementList only for List or MutableList"
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

    private fun Field.getTagNameOrNull(): String? {
        return if (this is Field.IsTag) {
            tagName.ifEmpty { null }
        } else {
            null
        }
    }

    private fun Field.getChildrenOrNull(): List<Field>? {
        return if (this is Field.IsTag) {
            children.ifEmpty { null }
        } else {
            null
        }
    }
}
