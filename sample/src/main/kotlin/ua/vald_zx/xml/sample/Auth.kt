package ua.vald_zx.xml.sample

import ua.vald_zx.xml.Element
import ua.vald_zx.xml.Path

data class Auth(
    @Element("UserId")
    var userId: String = "",
    @Path("Auth")
    @Element("Password", false)
    var password: String = "",
    @field:[Path("Auth") Element("Device", false)]
    var device: String = ""
)