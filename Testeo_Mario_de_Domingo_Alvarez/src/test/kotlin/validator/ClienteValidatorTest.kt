package validator

import error.ClienteError
import org.example.models.Cliente
import org.example.models.CuentaBancaria
import org.example.models.Tarjeta
import org.example.validator.DniValidator
import org.example.validator.TarjetaValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.util.*
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class ClienteValidatorTest {

    @Mock
    private lateinit var cuentaBancariaValidator: CuentaBancariaValidator

    @Mock
    private lateinit var dniValidator: DniValidator

    @Mock
    private lateinit var tarjetaValidator: TarjetaValidator

    @Mock
    private lateinit var clienteValidator: ClienteValidator

    @BeforeEach
    fun setUp() {
        // Inicializar el clienteValidator después de crear los mocks
        clienteValidator = ClienteValidator(cuentaBancariaValidator, dniValidator, tarjetaValidator)
    }

    @Test
    fun clienteValido() {
        val cliente = Cliente(
            id = UUID.randomUUID(),
            nombre = "Juan Pérez",
            dni = "12345678A",
            cuentaBancaria = CuentaBancaria("ES9121000418450200051332", 1000.0),
            tarjeta = Tarjeta("4532 7233 6544 2231", "12/26")
        )

        // Configuración de los mocks
        whenever(dniValidator.validarDni(cliente.dni)).thenReturn(true)
        whenever(cuentaBancariaValidator.validarCuenta(cliente.cuentaBancaria)).thenReturn(true)
        whenever(tarjetaValidator.validarTarjeta(cliente.tarjeta)).thenReturn(true)

        // Verificar que el cliente es válido
        assertTrue { clienteValidator.validarCliente(cliente) }
    }

    @Test
    fun nombreValido() {
        val nombre = "Mario"
        assertTrue(clienteValidator.validarNombre(nombre))
    }

    @Test
    fun nombreNoValido() {
        val nombre = "123"
        assertFailsWith<ClienteError.NombreInvalido>("El nombre del cliente es inválido.") {
            clienteValidator.validarNombre(nombre)
        }
    }

    @Test
    fun nombreConSimbolos() {
        val nombre = "Mario@123"
        assertFailsWith<ClienteError.NombreInvalido>("El nombre del cliente contiene caracteres inválidos.") {
            clienteValidator.validarNombre(nombre)
        }
    }

    @Test
    fun nombreConNumeros() {
        val nombre = "Mario123"
        assertFailsWith<ClienteError.NombreInvalido>("El nombre del cliente contiene números.") {
            clienteValidator.validarNombre(nombre)
        }
    }

    @Test
    fun nombreMenosdeDosLetras() {
        val nombre = "M"
        assertFailsWith<ClienteError.NombreInvalido>("El nombre del cliente debe tener al menos dos letras.") {
            clienteValidator.validarNombre(nombre)
        }
    }
}
