package ua.vald_zx.simplexml.ksp.xml.utils

internal class StringReader(raw: String) {
    private val iterator = raw.iterator()
    fun read(): Int {
        return if (iterator.hasNext()) {
            iterator.next().code
        } else -1
    }
}