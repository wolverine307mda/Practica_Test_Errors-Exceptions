package service.cache

import CacheImpl
import org.example.models.Cliente
import org.example.models.CuentaBancaria
import org.example.models.Tarjeta
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import service.cache.exception.CacheException
import java.util.*

@ExtendWith(MockitoExtension::class)
class CacheImplTest {

    private val cacheSize = 2
    private val cache: CacheImpl = CacheImpl(cacheSize)

    @Test
    fun eliminarDebeLanzarExcepcionSiNoExisteElValor() {
        val dni = "12345678A"
        val exception = assertThrows(CacheException.NotFound::class.java) {
            cache.remove(dni)
        }
        println("Mensaje de excepción: ${exception.message}") // Imprimir el mensaje para depurar
        assertTrue(exception.message?.contains("no se encontró en la caché") == true)
    }

    @Test
    fun debeLanzarExcepcionSiNoExisteElCliente() {
        val dni = "12345678A"
        val exception = assertThrows(CacheException.NotFound::class.java) {
            cache.get(dni)
        }
        println("Mensaje de excepción: ${exception.message}") // Imprimir el mensaje para depurar
        assertTrue(exception.message?.contains("no se encontró en la caché") == true)
    }

    @Test
    fun okYEliminarElClienteSiExiste() {
        val cliente = Cliente(
            id = UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )
        cache.put(cliente.dni, cliente)

        val removedCliente = cache.remove(cliente.dni)
        assertEquals(cliente, removedCliente)

        val exception = assertThrows(CacheException.NotFound::class.java) {
            cache.get(cliente.dni)
        }
        assertTrue(exception.message?.contains("no se encontró en la caché") == true)
    }

    @Test
    fun okSiExisteElValor() {
        val cliente = Cliente(
            id = UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )
        cache.put(cliente.dni, cliente)

        val cachedCliente = cache.get(cliente.dni)
        assertEquals(cliente, cachedCliente)
    }

    @Test
    fun guardarAgregarValorEnCache() {
        val cliente = Cliente(
            id = UUID.randomUUID(),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )

        val addedCliente = cache.put(cliente.dni, cliente)
        assertEquals(cliente, addedCliente)

        val cachedCliente = cache.get(cliente.dni)
        assertEquals(cliente, cachedCliente)
    }

    @Test
    fun eliminarElValorMasAntiguo() {
        val cliente1 = Cliente(
            id = UUID.randomUUID(),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )
        val cliente2 = Cliente(
            id = UUID.randomUUID(),
            nombre = "Ana",
            dni = "87654321B",
            tarjeta = Tarjeta("4532 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES7620770024003102586634", 1500.00)
        )
        val cliente3 = Cliente(
            id = UUID.randomUUID(),
            nombre = "Luis",
            dni = "11223344C",
            tarjeta = Tarjeta("4532 1234 5678 9012", "10/26"),
            cuentaBancaria = CuentaBancaria("ES1122334455667788990011", 200.00)
        )

        cache.put(cliente1.dni, cliente1)
        cache.put(cliente2.dni, cliente2)
        cache.put(cliente3.dni, cliente3)

        assertThrows(CacheException.NotFound::class.java) {
            cache.get(cliente1.dni)
        }
        assertEquals(cliente2, cache.get(cliente2.dni))
        assertEquals(cliente3, cache.get(cliente3.dni))
    }

    @Test
    fun limpiarDebeVaciarLaCache() {
        val cliente = Cliente(
            id = UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )
        cache.put(cliente.dni, cliente)

        cache.clear()

        assertThrows(CacheException.NotFound::class.java) {
            cache.get(cliente.dni)
        }
    }

    @Test
    fun noAgregarValorSiCacheEstaLlena() {
        val cliente1 = Cliente(
            id = UUID.randomUUID(),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )
        val cliente2 = Cliente(
            id = UUID.randomUUID(),
            nombre = "Ana",
            dni = "87654321B",
            tarjeta = Tarjeta("4532 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES7620770024003102586634", 1500.00)
        )
        val cliente3 = Cliente(
            id = UUID.randomUUID(),
            nombre = "Luis",
            dni = "11223344C",
            tarjeta = Tarjeta("4532 1234 5678 9012", "10/26"),
            cuentaBancaria = CuentaBancaria("ES1122334455667788990011", 200.00)
        )

        cache.put(cliente1.dni, cliente1)
        cache.put(cliente2.dni, cliente2)

        // Aquí la cache ya está llena, deberíamos eliminar el cliente más antiguo
        cache.put(cliente3.dni, cliente3)

        assertThrows(CacheException.NotFound::class.java) {
            cache.get(cliente1.dni)
        }
    }

    @Test
    fun actualizarValorEnCache() {
        val cliente = Cliente(
            id = UUID.randomUUID(),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )
        cache.put(cliente.dni, cliente)

        val updatedCliente = cliente.copy(nombre = "Pedro Actualizado")
        cache.put(updatedCliente.dni, updatedCliente)

        val cachedCliente = cache.get(updatedCliente.dni)
        assertEquals(updatedCliente, cachedCliente)
    }

    @Test
    fun obtenerValorDeCacheLuegoDeActualizar() {
        val cliente = Cliente(
            id = UUID.randomUUID(),
            nombre = "Pedro",
            dni = "12345678A",
            tarjeta = Tarjeta("4539 1488 0343 6467", "10/25"),
            cuentaBancaria = CuentaBancaria("ES9121000418450200051234", 89.00)
        )
        cache.put(cliente.dni, cliente)

        val updatedCliente = cliente.copy(nombre = "Pedro Actualizado")
        cache.put(updatedCliente.dni, updatedCliente)

        val cachedCliente = cache.get(updatedCliente.dni)
        assertEquals(updatedCliente, cachedCliente)
    }
}
