package ua.vald_zx.simplexml.ksp.processor

import com.squareup.kotlinpoet.ClassName

data class BeanToGenerate(
    val fullName: String,
    val name: String,
    val packagePath: String,
    val fields: MutableList<FieldToGenerate> = mutableListOf()
)

data class FieldToGenerate(
    val name: String,
    val type: FieldType,
    val path: String,
    val required: Boolean,
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