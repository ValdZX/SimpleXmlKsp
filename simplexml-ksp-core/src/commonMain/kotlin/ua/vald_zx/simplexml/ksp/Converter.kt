package ua.vald_zx.simplexml.ksp

interface Converter<T> {

    fun write(obj: T): String

    fun read(raw: String): T
}