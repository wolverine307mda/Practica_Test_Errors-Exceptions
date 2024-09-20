import org.example.models.Cliente
import org.lighthousegames.logging.logging
import service.cache.Cache
import service.cache.exception.CacheException

private val logger = logging()

class CacheImpl(
    private val size: Int  // Tamaño máximo de la caché
) : Cache<String, Cliente> {

    private val cache = mutableMapOf<String, Cliente>()  // Usamos un Map para almacenar la caché

    // Obtener un valor de la caché, lanzando una excepción si no se encuentra
    override fun get(key: String): Cliente {
        logger.debug { "Obteniendo valor de la cache para DNI: $key" }
        return cache[key] ?: throw CacheException.NotFound(key).also {
            logger.info { "No existe el cliente en la cache para DNI: $key" }
        }
    }

    // Guardar un valor en la caché
    override fun put(key: String, value: Cliente): Cliente {
        logger.debug { "Guardando cliente en la cache para DNI: $key" }
        // Si la caché está llena, eliminamos el cliente más antiguo (FIFO)
        if (cache.size >= size && !cache.containsKey(key)) {
            val oldestKey = cache.keys.first()
            logger.debug { "Eliminando el cliente más antiguo de la cache con DNI: $oldestKey" }
            cache.remove(oldestKey)
        }
        cache[key] = value
        logger.debug { "Cliente guardado en la cache con éxito" }
        return value
    }

    // Eliminar un valor de la caché, lanzando una excepción si no se encuentra
    override fun remove(key: String): Cliente {
        logger.debug { "Eliminando cliente de la cache para DNI: $key" }
        return cache.remove(key) ?: throw CacheException.NotFound(key).also {
            logger.info { "No existe el cliente en la cache para DNI: $key" }
        }
    }

    // Limpiar toda la caché
    override fun clear() {
        logger.debug { "Limpiando toda la cache" }
        cache.clear()
        logger.debug { "Cache limpiada con éxito" }
    }
}
