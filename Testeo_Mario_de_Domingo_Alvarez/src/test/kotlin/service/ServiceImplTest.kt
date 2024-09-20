package service

import Cache
import ServiceImpl
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import error.CuentaBancariaError
import error.DniError
import error.TarjetaError
import org.example.models.Cliente
import org.example.models.CuentaBancaria
import org.example.models.Tarjeta
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.lighthousegames.logging.logging
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import repository.Repository
import service.cache.error.CacheError
import validator.ClienteValidator
import java.util.*

private val logger = logging()
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
        whenever(cache.get(cliente.dni)).thenReturn(Ok(cliente))

        // Ejecutar
        val result = service.getClienteById(cliente.dni)

        // Aserciones
        assertTrue(result.isOk)
        assertEquals(cliente, result.value)

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
        whenever(cache.get(cliente.dni)).thenReturn(Err(CacheError.NotFound("No existe el cliente en la cache")))

        // Simular que el cliente está en el repositorio
        whenever(repository.findById(cliente.dni)).thenReturn(cliente)

        // Ejecutar
        val result = service.getClienteById(cliente.dni)

        // Aserciones
        assertTrue(result.isOk)
        assertEquals(cliente, result.value)

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
        assertTrue(result.isOk)
        assertEquals(cliente, result.value)

        // Verificar que el cliente se añadió a la cache
        verify(cache).put(cliente.dni, cliente)
    }

    @Test
    fun actualizaCcheCorrectamente() {
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
        assertTrue(result.isOk)
        assertEquals(cliente, result.value)

        // Verificar que el cliente se actualizó en la cache
        verify(cache).put(cliente.dni, cliente)
    }

    @Test
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
        val result = service.deleteCliente(cliente.dni)

        // Aserciones
        assertTrue(result.isOk)
        assertEquals(cliente, result.value)

        // Verificar que el cliente se eliminó de la cache
        verify(cache).remove(cliente.dni)
    }
    @Test
    fun noEncuentraClienteCache() {
        val dni = "12345678A"

        // Simular que el cliente no está en la cache
        whenever(cache.get(dni)).thenReturn(Err(CacheError.NotFound("No existe el cliente en la cache")))

        // Simular que el cliente no está en el repositorio
        whenever(repository.findById(dni)).thenReturn(null)

        // Ejecutar
        val result = service.getClienteById(dni)

        // Aserciones
        assertTrue(result.isErr)
        assertEquals("No se ha encontrado el cliente con DNI '$dni'.", result.error.message)
    }

    @Test
    fun noactualizaClienteInexistente() {
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
        // Ejecutar
        val result = service.updateCliente(dni, cliente)
        // Aserciones
        assertTrue(result.isErr)
        assertEquals("No se ha encontrado el cliente con DNI '$dni'.", result.error.message)
    }

    @Test
    fun errorAlEliminarInexistente() {
        val dni = "12345678A"
        // Simular que el cliente no está en el repositorio
        whenever(repository.delete(dni)).thenReturn(null)
        // Ejecutar
        val result = service.deleteCliente(dni)
        // Aserciones
        assertTrue(result.isErr)
        assertEquals("No se ha encontrado el cliente con DNI '$dni'.", result.error.message)
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

        // Ejecutar
        val result = service.saveCliente(cliente)

        // Aserciones
        assertTrue(result.isErr)
        assertEquals("No se ha encontrado el cliente con DNI 'Error al guardar el cliente: Error inesperado'.", result.error.message)
    }


    @Test
    fun noSeGuardaClienteConDniInvalido() {
        val clienteDniVacio = Cliente(
            id = UUID.randomUUID(),
            nombre = "Juan",
            dni = "",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )

        // Simular que la validación falla y lanza un ClienteError
        whenever(clienteValidator.validarCliente(clienteDniVacio)).thenThrow(RuntimeException(DniError.DniFormatoInvalido(clienteDniVacio.dni)))

        // Ejecutar
        val result = service.saveCliente(clienteDniVacio)

        val esperado = "No se ha encontrado el cliente con DNI 'Error al guardar el cliente: ${DniError.DniFormatoInvalido(clienteDniVacio.dni)}'."
        // Aserciones
        assertTrue(result.isErr)
        assertEquals(esperado, result.error.message)
    }

    @Test
    fun noSeGuardaClienteConTarjetaInvalida() {
        val clienteTarjetaInvalida = Cliente(
            id = UUID.randomUUID(),
            nombre = "Juan",
            dni = "12345678A",
            tarjeta = Tarjeta("", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )

        // Simular que la validación falla y lanza un ClienteError
        whenever(clienteValidator.validarCliente(clienteTarjetaInvalida)).thenThrow(RuntimeException(TarjetaError.TarjetaNumeroInvalido(clienteTarjetaInvalida.tarjeta.numero)))

        // Ejecutar
        val result = service.saveCliente(clienteTarjetaInvalida)

        val esperado = "No se ha encontrado el cliente con DNI 'Error al guardar el cliente: ${TarjetaError.TarjetaNumeroInvalido(clienteTarjetaInvalida.tarjeta.numero)}'."
        // Aserciones
        assertTrue(result.isErr)
        assertEquals(esperado, result.error.message)
    }

    @Test
    fun noSeGuardaClienteConCuentaInvalida() {
        val clienteCuentaInvalida = Cliente(
            id = UUID.randomUUID(),
            nombre = "Juan",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("", -100.0) // IBAN inválido y saldo negativo
        )

        // Simular que la validación falla y lanza un ClienteError
        whenever(clienteValidator.validarCliente(clienteCuentaInvalida)).thenThrow(RuntimeException(CuentaBancariaError.IbanFormatoIncorrecto(clienteCuentaInvalida.cuentaBancaria.iban)))

        // Ejecutar
        val result = service.saveCliente(clienteCuentaInvalida)

        val esperado = "No se ha encontrado el cliente con DNI 'Error al guardar el cliente: ${CuentaBancariaError.IbanFormatoIncorrecto(clienteCuentaInvalida.cuentaBancaria.iban)}'."

        // Aserciones
        assertTrue(result.isErr)
        assertEquals(esperado, result.error.message)
    }

    @Test
    fun noSeEncuentraClienteEnCacheYRepositorioRetornaError() {
        val dni = "12345678A"

        // Simular que el cliente no está en la cache
        whenever(cache.get(dni)).thenReturn(Err(CacheError.NotFound("No existe el cliente en la cache")))

        // Simular que el cliente no está en el repositorio
        whenever(repository.findById(dni)).thenReturn(null)

        // Ejecutar
        val result = service.getClienteById(dni)

        // Aserciones
        assertTrue(result.isErr)
        assertEquals("No se ha encontrado el cliente con DNI '$dni'.", result.error.message)
    }

    @Test
    fun errorEnValidacionCliente() {
        val cliente = Cliente(
            id = UUID.randomUUID(),
            nombre = "Juan",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )

        // Simular que la validación falla
        whenever(clienteValidator.validarCliente(cliente)).thenThrow(RuntimeException("Error de validación"))

        // Ejecutar
        val result = service.saveCliente(cliente)

        // Aserciones
        assertTrue(result.isErr)
        assertEquals("No se ha encontrado el cliente con DNI 'Error al guardar el cliente: Error de validación'.", result.error.message)
    }

    @Test
    fun eliminarClienteYaEliminado() {
        val dni = "12345678A"

        // Simular que el cliente ya ha sido eliminado
        whenever(repository.delete(dni)).thenReturn(null)

        // Ejecutar
        val result = service.deleteCliente(dni)

        // Aserciones
        assertTrue(result.isErr)
        assertEquals("No se ha encontrado el cliente con DNI '$dni'.", result.error.message)
    }

    @Test
    fun getClienteByDniDevuelveErrorSiCacheYRepositorioFallen() {
        val dni = "12345678A"

        // Simular que el cliente no está en la cache
        whenever(cache.get(dni)).thenReturn(Err(CacheError.NotFound("No existe el cliente en la cache")))

        // Simular que el cliente no está en el repositorio
        whenever(repository.findById(dni)).thenReturn(null)

        // Ejecutar
        val result = service.getClienteById(dni)

        // Aserciones
        assertTrue(result.isErr)
        assertEquals("No se ha encontrado el cliente con DNI '$dni'.", result.error.message)
    }

    @Test
    fun guardarClienteSinDatos() {
        val clienteVacio = Cliente(
            id = UUID.randomUUID(),
            nombre = "",
            dni = "",
            tarjeta = Tarjeta("", ""),
            cuentaBancaria = CuentaBancaria("", 0.0)
        )

        // Simular que la validación falla y lanza un ClienteError
        whenever(clienteValidator.validarCliente(clienteVacio)).thenThrow(RuntimeException("Cliente inválido"))

        // Ejecutar
        val result = service.saveCliente(clienteVacio)

        // Aserciones
        assertTrue(result.isErr)
        assertEquals("No se ha encontrado el cliente con DNI 'Error al guardar el cliente: Cliente inválido'.", result.error.message)
    }

    @Test
    fun guardarClienteYaExistente() {
        val cliente = Cliente(
            id = UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )

        // Simular que el cliente ya existe en el repositorio
        whenever(repository.save(cliente)).thenThrow(RuntimeException("El cliente ya existe"))

        // Ejecutar
        val result = service.saveCliente(cliente)

        // Aserciones
        assertTrue(result.isErr)
        assertEquals("No se ha encontrado el cliente con DNI 'Error al guardar el cliente: El cliente ya existe'.", result.error.message)
    }

    @Test
    fun eliminarClienteYaExistente() {
        val dni = "12345678A"

        // Simular que el cliente ya existe en el repositorio
        whenever(repository.delete(dni)).thenReturn(null)

        // Ejecutar
        val result = service.deleteCliente(dni)

        // Aserciones
        assertTrue(result.isErr)
        assertEquals("No se ha encontrado el cliente con DNI '$dni'.", result.error.message)
    }


}
