package service.cache.exception

sealed class CacheException(message: String) : Exception(message) {
    class NotFound(key: Any) : CacheException("El elemento con clave '$key' no se encontró en la caché.")
}
