package ua.vald_zx.simplexml.ksp.sample.beans

import ua.vald_zx.simplexml.ksp.Attribute
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.Path

data class Auth(
    @Element("UserId")
    var userId: String = "",
    @Element
    var location: String = "",
    @field:[Path("Auth") Element("Password")]
    var password: String = "",
    @field:[Path("Auth/House") Element("Device")]
    var device: String = "",
    @field:[Path("Auth/House/Device") Attribute("time")]
    var time: String = "",
    @field:[Path("Auth/House/Device") Attribute]
    var locale: String = ""
)