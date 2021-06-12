package ua.vald_zx.simplexml.ksp

interface Serializer<T> {
    fun deserialize(raw: String): T
    fun serialize(obj: T): String
}