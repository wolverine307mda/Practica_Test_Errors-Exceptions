import exceptions.ClienteException
import org.example.models.Cliente
import org.lighthousegames.logging.logging
import repository.Repository
import service.Service
import service.cache.Cache
import validator.ClienteValidator

private val logger = logging()

class ServiceImpl(
    private val repository: Repository,
    private val clienteValidator: ClienteValidator,
    private val cache: Cache<String, Cliente>
) : Service {

    override fun getClienteById(dni: String): Cliente {
        logger.debug { "getClienteById: dni = $dni" }

        // Primero intentamos obtener el cliente de la cache
        cache.get(dni)?.let { return it }

        // Si no está en la cache, lo buscamos en el repositorio
        return repository.findById(dni)?.let {
            cache.put(dni, it)  // Guardamos el cliente en la cache
            it
        } ?: throw ClienteException.ClienteNoEncontrado(dni)
    }

    override fun saveCliente(item: Cliente): Cliente {
        clienteValidator.validarCliente(item)  // Validación añadida
        val savedCliente = repository.save(item)
        cache.put(item.dni, savedCliente)  // Guardamos en la cache
        return savedCliente
    }

    override fun updateCliente(dni: String, item: Cliente): Cliente {
        clienteValidator.validarCliente(item)  // Validación añadida
        return repository.update(dni, item)?.let {
            cache.put(dni, it)  // Actualizamos en la cache
            it
        } ?: throw ClienteException.ClienteNoEncontrado(dni)
    }

    override fun deleteCliente(dni: String) {
        repository.delete(dni)?.let {
            cache.remove(dni)  // Eliminamos de la cache
        } ?: throw ClienteException.ClienteNoEncontrado(dni)
    }
}
