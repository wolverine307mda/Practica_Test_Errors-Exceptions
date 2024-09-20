package service.cache

import service.cache.exception.CacheException

interface Cache<K, V> {
    @Throws(CacheException::class)
    fun get(key: K): V

    @Throws(CacheException::class)
    fun put(key: K, value: V): V

    @Throws(CacheException::class)
    fun remove(key: K): V

    fun clear()
}
