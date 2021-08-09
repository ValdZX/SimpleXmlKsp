package ua.vald_zx.simplexml.ksp.processor.privitives.beans

import ua.vald_zx.simplexml.ksp.Element
import ua.vald_zx.simplexml.ksp.Root

@Root("RootString")
data class StringTag(
    @Element("Tag")
    val tag: String
)
