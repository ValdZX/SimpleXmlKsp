package ua.vald_zx.simplexml.ksp.processor

import com.squareup.kotlinpoet.ClassName

data class BeanToGenerate(
    val fullName: String,
    val name: String,
    val rootName: String,
    val packagePath: String,
    val fields: MutableList<FieldToGenerate> = mutableListOf()
)

data class FieldToGenerate(
    val fieldName: String,
    val tagName: String,
    val type: FieldType,
    val path: String,
    val required: Boolean,
)

data class FieldElement(
    val tagName: String,
    val fieldName: String = "",
    val isValueTag: Boolean = false,
    val children: MutableList<FieldElement> = mutableListOf()
)

data class ToRegistration(
    val beanClass: ClassName,
    val serializerClass: ClassName,
)

enum class FieldType {
    OBJECT,
    LIST,
    SET,
    MAP
}