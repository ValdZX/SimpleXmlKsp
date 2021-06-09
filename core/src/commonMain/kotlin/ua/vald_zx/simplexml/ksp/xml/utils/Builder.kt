package ua.vald_zx.simplexml.ksp.xml.utils

object Builder {
    class MapBuilder<K, V>(private val map: MutableMap<K, V>) {
        fun put(k: K, v: V): MapBuilder<K, V> {
            map[k] = v
            return this
        }

        fun build(): Map<K, V> {
            return map
        }
    }

    fun <K, V> newMutableMap(): MapBuilder<K, V> {
        return MapBuilder(mutableMapOf())
    }
}