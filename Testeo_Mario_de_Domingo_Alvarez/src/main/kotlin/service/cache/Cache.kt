import service.cache.error.CacheError
import com.github.michaelbull.result.Result

interface Cache<K, V> {
    fun get(key: K): Result<V, CacheError>
    fun put(key: K, value: V): Result<V, CacheError>
    fun remove(key: K): Result<V, CacheError>
    fun clear(): Result<Unit, Nothing>
}