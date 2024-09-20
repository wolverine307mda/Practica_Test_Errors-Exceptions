import org.example.models.Cliente
import org.example.models.CuentaBancaria
import org.example.models.Tarjeta
import repository.Repository
import org.example.repository.RepositoryImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
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

    @Test
    fun delete() {
        val cliente = Cliente(UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"), "Juan", "12345678A", CuentaBancaria("ES1234567890123456789012", 1000.0), Tarjeta("4532 7233 6544 2231", "12/26"))
        repository.save(cliente)

        val result = repository.delete("12345678A")

        assertNotNull(result)
        assertEquals(cliente, result)
        assertNull(repository.findById("12345678A"))
    }

    @Test
    fun updateClienteWhenClienteNotFound() {
        // Given
        val dni = "12345678A"
        val cliente = Cliente(
            UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"),
            "Juan",
            dni,
            CuentaBancaria("ES1234567890123456789012", 1000.0),
            Tarjeta("4532 7233 6544 2231", "12/26")
        )

        // When
        val result = repository.update(dni, cliente)

        // Then
        assertNull(result, "El cliente debería ser nulo cuando no se encuentra en el repositorio.")
    }

    @Test
    fun findByIdCuandoNoExiste() {
        val result = repository.findById("12345678A")
        assertNull(result, "El resultado debería ser nulo si el cliente no existe en el repositorio.")
    }

    @Test
    fun deleteCuandoClienteNoExiste() {
        val result = repository.delete("12345678A")
        assertNull(result, "El resultado debería ser nulo si el cliente no existe.")
    }

    @Test
    fun actualizarClienteConDniInvalido() {
        val cliente = Cliente(UUID.fromString("63591beb-d620-4dcf-a719-3a2c4ed88f02"), "Juan", "12345678A", CuentaBancaria("ES1234567890123456789012", 1000.0), Tarjeta("4532 7233 6544 2231", "12/26"))
        repository.save(cliente)

        // Intentar actualizar con un DNI inválido
        val invalidDni = "87654321B"
        val updatedCliente = cliente.copy(nombre = "Juan Actualizado")
        val result = repository.update(invalidDni, updatedCliente)

        assertNull(result, "El resultado debería ser nulo si se intenta actualizar un cliente con un DNI que no existe.")
    }

    @Test
    fun buscarClientePorDniInvalido() {
        val result = repository.findById("87654321B")
        assertNull(result, "El resultado debería ser nulo si se busca un cliente con un DNI que no existe.")
    }

    @Test
    fun guardarYRecuperarMultipleClientes() {
        val clientes = listOf(
            Cliente(UUID.randomUUID(), "Juan", "12345678A", CuentaBancaria("ES1234567890123456789012", 1000.0), Tarjeta("4532 7233 6544 2231", "12/26")),
            Cliente(UUID.randomUUID(), "Maria", "23456789B", CuentaBancaria("ES1234567890123456789013", 1500.0), Tarjeta("4532 7233 6544 2232", "12/27")),
            Cliente(UUID.randomUUID(), "Carlos", "34567890C", CuentaBancaria("ES1234567890123456789014", 2000.0), Tarjeta("4532 7233 6544 2233", "12/28"))
        )

        // Guardar múltiples clientes
        clientes.forEach { repository.save(it) }

        // Verificar que todos se pueden recuperar correctamente
        clientes.forEach { cliente ->
            val result = repository.findById(cliente.dni)
            assertNotNull(result)
            assertEquals(cliente, result)
        }
    }

}
