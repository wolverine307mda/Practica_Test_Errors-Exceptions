import org.example.models.Cliente
import org.example.models.CuentaBancaria
import org.example.models.Tarjeta
import exceptions.ClienteException
import org.example.repository.RepositoryImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import repository.Repository
import java.util.*

class RepositoryImplTest {

    private val repository: Repository = RepositoryImpl()

    @Test
    fun findById() {
        val cliente = Cliente(UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"), "Juan", "12345678A", CuentaBancaria("ES1234567890123456789012", 1000.0), Tarjeta("4532 7233 6544 2231", "12/26"))
        repository.save(cliente)

        val result = repository.findById("12345678A")

        assertNotNull(result)
        assertEquals(cliente, result)
    }

    @Test
    fun save() {
        val cliente = Cliente(UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"), "Juan", "12345678A", CuentaBancaria("ES1234567890123456789012", 1000.0), Tarjeta("4532 7233 6544 2231", "12/26"))
        val result = repository.save(cliente)

        assertEquals(cliente, result)
        assertEquals(cliente, repository.findById("12345678A"))
    }

    @Test
    fun update() {
        val cliente = Cliente(UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"), "Juan", "12345678A", CuentaBancaria("ES1234567890123456789012", 1000.0), Tarjeta("4532 7233 6544 2231", "12/26"))
        repository.save(cliente)

        val updatedCliente = cliente.copy(nombre = "Juan Updated")
        val result = repository.update("12345678A", updatedCliente)

        assertNotNull(result)
        assertEquals(updatedCliente, result)
        assertEquals(updatedCliente, repository.findById("12345678A"))
    }

    /*@Test
    fun delete() {
        val cliente = Cliente(UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"), "Juan", "12345678A", CuentaBancaria("ES1234567890123456789012", 1000.0), Tarjeta("4532 7233 6544 2231", "12/26"))
        repository.save(cliente)

        val result = repository.delete("12345678A")
        assertNotNull(result)
        assertEquals(cliente, result)

        // Aquí debes asegurarte de que findById lanza la excepción al no encontrar el cliente
        val exception = assertThrows<ClienteException.ClienteNoEncontrado> {
            repository.findById("12345678A")
        }
        assertTrue(exception.message?.contains("no encontrado") == true) // Asegúrate de que el mensaje coincide
    }

    @Test
    fun updateClienteWhenClienteNotFound() {
        val dni = "12345678A"
        val cliente = Cliente(
            UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"),
            "Juan",
            dni,
            CuentaBancaria("ES1234567890123456789012", 1000.0),
            Tarjeta("4532 7233 6544 2231", "12/26")
        )

        // Asegúrate de que el cliente NO se guarda antes de llamar a update
        val exception = assertThrows<ClienteException.ClienteNoEncontrado> {
            repository.update(dni, cliente) // Aquí debe lanzarse la excepción
        }
        assertTrue(exception.message?.contains("no encontrado") == true)
    }


    @Test
    fun findByIdCuandoNoExiste() {
        val exception = assertThrows<ClienteException.ClienteNoEncontrado> {
            repository.findById("12345678A")
        }
        assertTrue(exception.message?.contains("no encontrado") == true) // Verifica el mensaje
    }

    @Test
    fun deleteCuandoClienteNoExiste() {
        val exception = assertThrows<ClienteException.ClienteNoEncontrado> {
            repository.delete("12345678A")
        }
        assertTrue(exception.message?.contains("no encontrado") == true) // Verifica el mensaje
    }

    @Test
    fun actualizarClienteConDniInvalido() {
        val cliente = Cliente(UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"), "Juan", "12345678A", CuentaBancaria("ES1234567890123456789012", 1000.0), Tarjeta("4532 7233 6544 2231", "12/26"))
        repository.save(cliente)

        val invalidDni = "87654321B"
        val exception = assertThrows<ClienteException.ClienteNoEncontrado> {
            repository.update(invalidDni, cliente.copy(nombre = "Juan Actualizado"))
        }
        assertTrue(exception.message?.contains("no encontrado") == true) // Verifica el mensaje
    }

    @Test
    fun buscarClientePorDniInvalido() {
        val exception = assertThrows<ClienteException.ClienteNoEncontrado> {
            repository.findById("87654321B")
        }
        assertTrue(exception.message?.contains("no encontrado") == true) // Verifica el mensaje
    }*/



    @Test
    fun guardarYRecuperarMultipleClientes() {
        val clientes = listOf(
            Cliente(UUID.randomUUID(), "Juan", "12345678A", CuentaBancaria("ES1234567890123456789012", 1000.0), Tarjeta("4532 7233 6544 2231", "12/26")),
            Cliente(UUID.randomUUID(), "Maria", "23456789B", CuentaBancaria("ES1234567890123456789013", 1500.0), Tarjeta("4532 7233 6544 2232", "12/27")),
            Cliente(UUID.randomUUID(), "Carlos", "34567890C", CuentaBancaria("ES1234567890123456789014", 2000.0), Tarjeta("4532 7233 6544 2233", "12/28"))
        )

        clientes.forEach { repository.save(it) }

        clientes.forEach { cliente ->
            val result = repository.findById(cliente.dni)
            assertNotNull(result)
            assertEquals(cliente, result)
        }
    }

}
