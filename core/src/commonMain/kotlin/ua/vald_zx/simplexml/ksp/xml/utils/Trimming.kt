package ua.vald_zx.simplexml.ksp.xml.utils


internal interface Trim {
    fun trim(input: String): String
    fun isWhitespace(c: Char): Boolean
}

internal object Trimming {
    class NativeTrimmer : Trim {
        override fun trim(input: String): String {
            return input.trim { it <= ' ' }
        }

        override fun isWhitespace(c: Char): Boolean {
            return c.isWhitespace()
        }
    }
}