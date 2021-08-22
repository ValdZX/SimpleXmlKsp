package ua.vald_zx.simplexml.ksp.test.extension

import ua.vald_zx.simplexml.ksp.Element

open class AbsBean<T1, T2> {
    @Element
    var somObject1: T1? = null

    @Element
    open var somObject2: T2? = null
}

open class ExtBean<T1, T2, T3> : AbsBean<T1, T2>() {
    @Element
    override var somObject2: T2? = null

    @Element
    var somObject3: T2? = null

    @Element
    var somObject4: T3? = null
}