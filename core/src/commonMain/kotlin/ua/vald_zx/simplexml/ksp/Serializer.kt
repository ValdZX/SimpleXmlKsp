package ua.vald_zx.simplexml.ksp

interface Serializer<T> {
    fun serialize(obj: T): String

    fun deserialize(raw: String): T
}