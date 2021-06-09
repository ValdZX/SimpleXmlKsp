package ua.vald_zx.simplexml.ksp.xml

import ua.vald_zx.simplexml.ksp.xml.error.InvalidXml
import ua.vald_zx.simplexml.ksp.xml.parsing.EventParser
import ua.vald_zx.simplexml.ksp.xml.utils.*

object XmlStreamReader {
    fun toXmlStream(input: InputStreamReader, parser: EventParser, trimmer: Trim, escaper: UnEscape) {
        var isStart = true
        var str: String
        while (XmlParse.readLine(input, Constants.XML_TAG_START).also { str = it ?: "" } != null) {
            val text = trimmer.trim(str)
            if (text.isNotEmpty()) {
                if (isStart) throw InvalidXml("XML contains non-whitespace characters before opening tag")
                parser.someText(escaper.unescape(text))
            }
            isStart = false
            val endTo = XmlParse.readLine(input, Constants.XML_TAG_END)
            str = endTo?.let { trimmer.trim(endTo) } ?: ""
            if (str.isEmpty()) throw InvalidXml("Unclosed tag")
            if (str.startsWith(Constants.XML_START_COMMENT)) {
                if (str.endsWith(Constants.XML_END_COMMENT)) continue
                XmlParse.readUntil(input, Constants.XML_END_COMMENT + ">")
                continue
            }
            if (str[0] == Constants.XML_PROLOG) continue
            if (str[0] == Constants.XML_SELF_CLOSING) parser.endNode() else {
                val name: String = XmlParse.getNameOfTag(str)
                if (str.length == name.length) {
                    parser.startNode(str, mutableMapOf())
                    continue
                }
                val beginAttr = name.length
                val end = str.length
                if (str.endsWith(Constants.FORWARD_SLASH)) {
                    parser.startNode(name, xmlToAttributes(str.substring(beginAttr, end - 1), trimmer, escaper))
                    parser.endNode()
                } else {
                    parser.startNode(name, xmlToAttributes(str.substring(beginAttr + 1, end), trimmer, escaper))
                }
            }
        }
    }

    private fun xmlToAttributes(inputString: String, trimmer: Trim, escaper: UnEscape): MutableMap<String, String> {
        val attributes = mutableMapOf<String, String>()
        xmlParseTags(inputString, trimmer) { name, value ->
            attributes[name] = escaper.unescape(value)
        }
        return attributes
    }

    private fun xmlParseTags(
        inputString: String,
        trimmer: Trim,
        findTag: (name: String, value: String) -> Unit
    ) {
        var input = inputString
        while (input.isNotEmpty()) {
            val startName: Int = XmlParse.indexOfNonWhitespaceChar(input, 0, trimmer)
            if (startName == -1) break
            val equals: Int = input.indexOf(Constants.CHAR_EQUALS, startName + 1)
            if (equals == -1) break
            val name = trimmer.trim(input.substring(startName, equals))
            input = input.substring(equals + 1)
            var startValue: Int = XmlParse.indexOfNonWhitespaceChar(input, 0, trimmer)
            if (startValue == -1) break
            var endValue: Int
            val value: String?
            if (input[startValue] == Constants.CHAR_DOUBLE_QUOTE) {
                startValue++
                endValue = input.indexOf(Constants.CHAR_DOUBLE_QUOTE, startValue)
                if (endValue == -1) endValue = input.length - 1
                value = trimmer.trim(input.substring(startValue, endValue))
            } else {
                endValue = XmlParse.indexOfWhitespaceChar(input, startValue + 1, trimmer)
                if (endValue == -1) endValue = input.length - 1
                value = trimmer.trim(input.substring(startValue, endValue + 1))
            }
            input = input.substring(endValue + 1)
            findTag(name, value)
        }
    }
}