package ua.vald_zx.simplexml.ksp.xml.parsing

interface EventParser {
    fun startNode(name: String, attrs: MutableMap<String, String>)
    fun endNode()
    fun someText(txt: String)
}