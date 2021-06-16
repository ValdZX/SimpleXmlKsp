package ua.vald_zx.simplexml.ksp


@Target(AnnotationTarget.CLASS)
annotation class Root(val name: String)

@Target(AnnotationTarget.FIELD)
annotation class Attribute(val name: String = "", val required: Boolean = false)

@Target(AnnotationTarget.FIELD)
annotation class Path(val path: String)

@Target(AnnotationTarget.FIELD)
annotation class Element(val name: String = "", val required: Boolean = false)

@Target(AnnotationTarget.FIELD)
annotation class ElementList(
    val name: String = "",
    val inline: Boolean = false,
    val required: Boolean = false
)
