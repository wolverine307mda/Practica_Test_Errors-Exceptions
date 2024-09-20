import ServiceImpl
import exceptions.ClienteException
import org.example.models.Cliente
import org.example.models.CuentaBancaria
import org.example.models.Tarjeta
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import repository.Repository
import service.cache.Cache
import validator.ClienteValidator
import java.util.*

@ExtendWith(MockitoExtension::class)
class ServiceImplTest {

    @Mock
    lateinit var repository: Repository

    @Mock
    lateinit var clienteValidator: ClienteValidator

    @Mock
    lateinit var cache: Cache<String, Cliente>

    lateinit var service: ServiceImpl

    @BeforeEach
    fun setup() {
        service = ServiceImpl(repository, clienteValidator, cache)
    }

    @Test
    fun getByDniDesdeCache() {
        val cliente = Cliente(
            id = UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )

        // Simular que el cliente está en la cache
        whenever(cache.get(cliente.dni)).thenReturn(cliente)

        // Ejecutar
        val result = service.getClienteById(cliente.dni)

        // Aserciones
        assertEquals(cliente, result)

        // Verificar que el repositorio no fue llamado
        verify(repository, times(0)).findById(cliente.dni) // Verificar que no fue llamado
    }


    @Test
    fun cogeClienteDeRepositorioSiNoEstaEnCache() {
        val cliente = Cliente(
            id = UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )

        // Simular que el cliente no está en la cache
        whenever(cache.get(cliente.dni)).thenReturn(null)

        // Simular que el cliente está en el repositorio
        whenever(repository.findById(cliente.dni)).thenReturn(cliente)

        // Ejecutar
        val result = service.getClienteById(cliente.dni)

        // Aserciones
        assertEquals(cliente, result)

        // Verificar que primero se intentó obtener de la cache y luego del repositorio
        verify(cache).get(cliente.dni)
        verify(repository).findById(cliente.dni)

        // Verificar que el cliente se añadió a la cache
        verify(cache).put(cliente.dni, cliente)
    }


    @Test
    fun clienteSeGuardaEnCache() {
        val cliente = Cliente(
            id = UUID.randomUUID(),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )

        // Preparar mocks
        whenever(clienteValidator.validarCliente(cliente)).thenReturn(true)
        whenever(repository.save(cliente)).thenReturn(cliente)

        // Ejecutar
        val result = service.saveCliente(cliente)

        // Aserciones
        assertEquals(cliente, result)

        // Verificar que el cliente se añadió a la cache
        verify(cache).put(cliente.dni, cliente)
    }

    @Test
    fun actualizaCacheCorrectamente() {
        val cliente = Cliente(
            id = UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )

        // Preparar mocks
        whenever(clienteValidator.validarCliente(cliente)).thenReturn(true)
        whenever(repository.update(cliente.dni, cliente)).thenReturn(cliente)

        // Ejecutar
        val result = service.updateCliente(cliente.dni, cliente)

        // Aserciones
        assertEquals(cliente, result)

        // Verificar que el cliente se actualizó en la cache
        verify(cache).put(cliente.dni, cliente)
    }

    /*@Test
    fun eliminarExitoso() {
        val cliente = Cliente(
            id = UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )

        // Preparar mocks
        whenever(repository.delete(cliente.dni)).thenReturn(cliente)

        // Ejecutar
        service.deleteCliente(cliente.dni)

        // Verificar que el cliente se eliminó de la cache
        verify(cache).remove(cliente.dni)
    }*/

    @Test
    fun noEncuentraClienteCacheYLanzaExcepcion() {
        val dni = "12345678A"

        // Simular que el cliente no está en la cache ni en el repositorio
        whenever(cache.get(dni)).thenReturn(null)
        whenever(repository.findById(dni)).thenReturn(null)

        // Ejecutar y verificar la excepción lanzada
        val exception = assertThrows<ClienteException.ClienteNoEncontrado> {
            service.getClienteById(dni)
        }

        assertEquals("No se ha encontrado el cliente con DNI '$dni'.", exception.message)
    }

    @Test
    fun noActualizaClienteInexistente() {
        val dni = "12345678A"
        val cliente = Cliente(
            id = UUID.randomUUID(),
            nombre = "Pedro",
            dni = dni,
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )
        // Simular que el cliente no está en el repositorio
        whenever(repository.update(dni, cliente)).thenReturn(null)

        // Ejecutar y verificar la excepción lanzada
        val exception = assertThrows<ClienteException.ClienteNoEncontrado> {
            service.updateCliente(dni, cliente)
        }

        assertEquals("No se ha encontrado el cliente con DNI '$dni'.", exception.message)
    }

    @Test
    fun errorAlEliminarClienteInexistente() {
        val dni = "12345678A"

        // Simular que el cliente no está en el repositorio
        whenever(repository.delete(dni)).thenReturn(null)

        // Ejecutar y verificar la excepción lanzada
        val exception = assertThrows<ClienteException.ClienteNoEncontrado> {
            service.deleteCliente(dni)
        }

        assertEquals("No se ha encontrado el cliente con DNI '$dni'.", exception.message)
    }

    @Test
    fun errorSiNoSePuedeGuardarCliente() {
        val cliente = Cliente(
            id = UUID.randomUUID(),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )

        // Simular que la validación es exitosa
        whenever(clienteValidator.validarCliente(cliente)).thenReturn(true)
        // Simular que el repositorio lanza una excepción inesperada
        whenever(repository.save(cliente)).thenThrow(RuntimeException("Error inesperado"))

        // Ejecutar y verificar la excepción lanzada
        val exception = assertThrows<RuntimeException> {
            service.saveCliente(cliente)
        }

        assertEquals("Error inesperado", exception.message)
    }
}
