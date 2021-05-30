package ua.vald_zx.simplexml.ksp.sample

import ua.vald_zx.xml.*

fun main() {
    println(Auth().toXml())
    println(XmlBean().toXml())
    println("AuthXML".parseAuth())
    println("XmlBeanXML".parseXmlBean())
    println("that all folks")
}