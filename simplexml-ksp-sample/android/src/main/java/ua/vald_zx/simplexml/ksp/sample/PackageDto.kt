package ua.vald_zx.simplexml.ksp.sample

import ua.vald_zx.simplexml.ksp.Attribute
import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.Path
import ua.vald_zx.simplexml.ksp.Root

@Root("Package")
data class PackageDto(
    @Attribute(name = "service")
    val serviceName: String,
    @Element(name = "Token")
    val token: String,
    @Element(name = "Location")
    val location: String,
    @Path("Location")
    @Attribute(name = "lat")
    val latitude: Double,
    @Path("Location")
    @Attribute(name = "lng")
    val longitude: Double,
)