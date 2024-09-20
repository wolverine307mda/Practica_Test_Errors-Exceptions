import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.example.models.Cliente
import org.lighthousegames.logging.logging
import service.cache.error.CacheError

private val logger = logging()

open class CacheImpl(
    private val size: Int
) : Cache<String, Cliente> {  // Cambiamos la clave a String
    private val cache = mutableMapOf<String, Cliente>()  // Clave ahora es String (DNI)

    override fun get(key: String): Result<Cliente, CacheError> {
        logger.debug { "Obteniendo valor de la cache para DNI: $key" }
        return if (cache.containsKey(key)) {
            logger.debug { "Cliente obtenido de la cache con éxito" }
            Ok(cache.getValue(key))
        } else {
            logger.info { "No existe el cliente en la cache para DNI: $key" }
            Err(CacheError.NotFound("No existe el cliente en la cache"))
        }
    }

    override fun put(key: String, value: Cliente): Result<Cliente, Nothing> {
        logger.debug { "Guardando cliente en la cache para DNI: $key" }
        if (cache.size >= size && !cache.containsKey(key)) {
            logger.debug { "Eliminando el cliente más antiguo de la cache" }
            cache.remove(cache.keys.first())  // FIFO: eliminamos el primer cliente añadido
        }
        cache[key] = value
        logger.debug { "Cliente guardado en la cache con éxito" }
        return Ok(value)
    }

    override fun remove(key: String): Result<Cliente, CacheError> {
        logger.debug { "Eliminando cliente de la cache para DNI: $key" }
        return if (cache.containsKey(key)) {
            logger.debug { "Cliente eliminado de la cache con éxito" }
            Ok(cache.remove(key)!!)
        } else {
            logger.info { "No existe el cliente en la cache para DNI: $key" }
            Err(CacheError.NotFound("No existe el cliente en la cache"))
        }
    }

    override fun clear(): Result<Unit, Nothing> {
        logger.debug { "Limpiando toda la cache" }
        cache.clear()
        logger.debug { "Cache limpiada con éxito" }
        return Ok(Unit)
    }
}