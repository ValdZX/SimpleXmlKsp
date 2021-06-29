package ua.vald_zx.simplexml.ksp.sample.beans

import ua.vald_zx.simplexml.ksp.Attribute
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.ElementList
import ua.vald_zx.simplexml.ksp.Path

data class Auth(
    @Element("UserId")
    var userId: String,
    @Element
    var location: String = "",
    @field:[Path("Auth") Element("Password")]
    var password: String,
    @field:[Path("Auth/House") Element("Device")]
    var device: String = "",
    @field:[Path("Auth/House/Device") Attribute("time")]
    var time: String = "",
    @field:[Path("Auth/House/Device") Attribute]
    var locale: String = "",
    @ElementList(name = "Phones", entry = "Phone", required = false)
    var phones: List<String> = mutableListOf()
) {
    @Element(required = false)
    var legend: String? = null

    @Element
    var legend2: String? = null

    @Element(required = false)
    var legend3: String? = null

    @field:[Path("legend3") Attribute(required = false)]
    var legend4: String? = null

    @ElementList(inline = true, entry = "Address", required = false)
    var addresses: List<String> = mutableListOf()
}