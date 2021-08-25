package ua.vald_zx.simplexml.ksp

import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS)
annotation class Root(val name: String)

@Target(AnnotationTarget.FIELD)
annotation class Attribute(val name: String = "", val required: Boolean = true)

@Target(AnnotationTarget.FIELD)
annotation class Path(val path: String)

@Target(AnnotationTarget.FIELD)
annotation class Element(val name: String = "", val required: Boolean = true)

@Target(AnnotationTarget.FIELD)
annotation class ElementList(
    val name: String = "",
    val entry: String = "",
    val required: Boolean = true,
    val inline: Boolean = false
)

@Target(AnnotationTarget.FIELD)
annotation class ElementMap(
    val name: String = "",
    val key: String = "",
    val value: String = "",
    val inline: Boolean = false,
    val required: Boolean = true
)

@Target(AnnotationTarget.FIELD)
annotation class Convert(val converter: KClass<out Converter<*>>)
