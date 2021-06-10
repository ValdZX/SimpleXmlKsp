package ua.vald_zx.simplexml.ksp

interface Serializer<T> {
    fun deserialize(data: String): T
    fun serialize(obj: T): String
}