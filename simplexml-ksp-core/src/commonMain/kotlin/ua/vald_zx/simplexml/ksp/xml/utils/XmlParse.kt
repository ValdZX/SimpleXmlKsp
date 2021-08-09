package ua.vald_zx.simplexml.ksp.xml.utils


internal object XmlParse {

    fun getNameOfTag(tag: String): String {
        var offset = 0
        while (offset < tag.length) {
            if (tag[offset] == Constants.CHAR_SPACE || tag[offset] == Constants.CHAR_FORWARD_SLASH) break
            offset++
        }
        return tag.substring(0, offset)
    }

    fun readLine(input: StringReader, end: Char): String? {
        val chars = StringBuilder()
        var data: Int
        while (input.read().also { data = it } != -1) {
            if (data == end.code) break
            chars.append(data.toChar())
        }
        return if (data == -1) null else chars.toString()
    }

    fun readUntil(input: StringReader, end: String): String? {
        val chars = StringBuilder()
        for (i in end.indices) {
            val data = input.read()
            if (data == -1) return null
            chars.append(data.toChar())
        }
        if (isEndReached(chars, end)) return chars.toString()
        var data: Int
        while (input.read().also { data = it } != -1) {
            chars.append(data.toChar())
            if (isEndReached(chars, end)) return chars.toString()
        }
        return null
    }

    private fun isEndReached(chars: StringBuilder, postfix: String): Boolean {
        val start = chars.length - postfix.length
        val end = chars.length
        return chars.substring(start, end) == postfix
    }

    fun indexOfNonWhitespaceChar(input: String, offset: Int, trimmer: Trim): Int {
        for (i in offset until input.length) {
            val at = input[i]
            if (trimmer.isWhitespace(at)) continue
            return i
        }
        return -1
    }

    fun indexOfWhitespaceChar(input: String, offset: Int, trimmer: Trim): Int {
        for (i in offset until input.length) {
            val at = input[i]
            if (trimmer.isWhitespace(at)) return i
        }
        return -1
    }
}