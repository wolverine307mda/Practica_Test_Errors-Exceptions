package service.cache.error

sealed class CacheError(val message: String) {
    class NotFound(key: Any) : CacheError("El elemento con clave '$key' no se encontró en la caché.")
    // class CacheFull: CacheError("La caché está llena y el elemento más antiguo ha sido eliminado.")
}
