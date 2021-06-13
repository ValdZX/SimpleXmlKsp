package ua.vald_zx.simplexml.ksp

annotation class Root(val name: String)
annotation class Element(val name: String, val required: Boolean = true)
annotation class Path(val path: String)
