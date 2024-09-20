import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.onSuccess
import error.ClienteError
import org.example.models.Cliente
import org.example.service.Service
import org.lighthousegames.logging.logging
import repository.Repository
import validator.ClienteValidator

private val logger = logging()

class ServiceImpl(
    private val repository: Repository,
    private val clienteValidator: ClienteValidator,
    private val cache: Cache<String, Cliente>  // Cache utiliza DNI (String) como clave
) : Service {

    override fun getClienteById(dni: String): Result<Cliente, ClienteError> {
        logger.debug { "getClienteById: dni = $dni" }

        // Primero intentamos obtener el cliente de la cache
        cache.get(dni).onSuccess { return Ok(it) }

        // Si no está en la cache, lo buscamos en el repositorio
        return repository.findById(dni)?.let {
            // Guardamos el cliente en la cache antes de devolverlo
            cache.put(dni, it)
            Ok(it)
        } ?: Err(ClienteError.ClienteNoEncontrado(dni))
    }

    override fun saveCliente(item: Cliente): Result<Cliente, ClienteError> {
        return try {
            clienteValidator.validarCliente(item)
            val savedCliente = repository.save(item) ?: throw RuntimeException("Error al guardar el cliente: cliente no guardado")
            cache.put(item.dni, savedCliente)
            Ok(savedCliente)
        } catch (e: ClienteError) {
            Err(e) // Devolver el error específico de validación
        } catch (e: RuntimeException) {
            // Manejo de errores en el caso de que el cliente no se guarde por alguna razón
            Err(ClienteError.ClienteNoEncontrado("Error al guardar el cliente: ${e.message}"))
        }
    }

    override fun updateCliente(dni: String, item: Cliente): Result<Cliente, ClienteError> {
        return try {
            clienteValidator.validarCliente(item)  // Validación añadida
            repository.update(dni, item)?.let {
                cache.put(dni, it)  // Actualizamos en la cache
                Ok(it)
            } ?: Err(ClienteError.ClienteNoEncontrado(dni))
        } catch (e: ClienteError) {
            Err(e)
        }
    }

    override fun deleteCliente(dni: String): Result<Cliente, ClienteError> {
        return repository.delete(dni)?.let {
            cache.remove(dni)  // Eliminamos de la cache
            Ok(it)
        } ?: Err(ClienteError.ClienteNoEncontrado(dni))
    }
}
