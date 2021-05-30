package ua.vald_zx.simplexml.ksp.sample

import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.Path

data class Auth(
    @Element("UserId")
    var userId: String = "",
    @Path("Auth")
    @Element("Password", false)
    var password: String = "",
    @field:[Path("Auth") Element("Device", false)]
    var device: String = ""
)