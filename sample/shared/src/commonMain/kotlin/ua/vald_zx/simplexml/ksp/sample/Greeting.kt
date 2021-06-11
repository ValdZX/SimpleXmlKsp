package ua.vald_zx.simplexml.ksp.sample

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}