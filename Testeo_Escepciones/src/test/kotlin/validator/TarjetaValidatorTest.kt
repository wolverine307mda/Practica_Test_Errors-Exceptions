import exceptions.TarjetaException
import org.example.models.Tarjeta
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import validator.TarjetaValidator

class TarjetaValidatorTest {
    private val validator = TarjetaValidator()

    @Test
    fun validarNumeroTarjeta() {
        val numeroTarjeta = "4532 7233 6544 2231"
        assertTrue(validator.validarNumeroTarjeta(numeroTarjeta))
    }

    @Test
    fun validarFechaCaducidad() {
        val fechaCaducidad = "12/25"
        assertTrue(validator.validarFechaCaducidad(fechaCaducidad))
    }

    @Test
    fun validarTarjeta() {
        val numeroTarjeta = "4532 7233 6544 2231"
        val tarjeta = Tarjeta(numeroTarjeta, "12/25")
        assertTrue(validator.validarTarjeta(tarjeta))
    }

    @Test
    fun invalidoNumeroTarjeta() {
        val numeroTarjeta = "4532 7233 6544 2232"
        assertFalse(validator.validarNumeroTarjeta(numeroTarjeta))
    }

    @Test
    fun invalidaFechaCaducidad() {
        val fechaCaducidad = "1111"
        assertFalse(validator.validarFechaCaducidad(fechaCaducidad))
    }

    @Test
    fun invalidaTarjeta() {
        val numeroTarjeta = "4532 7233 6544 2232"
        val tarjeta = Tarjeta(numeroTarjeta, "12/24")
        assertThrows(TarjetaException.TarjetaNumeroInvalido::class.java) {
            validator.validarTarjeta(tarjeta)
        }
    }

    @Test
    fun numeroTarjetaNulo() {
        val numeroTarjeta = ""
        assertFalse(validator.validarNumeroTarjeta(numeroTarjeta))
    }

    @Test
    fun fechaCaducidadNula() {
        val fechaCaducidad = ""
        assertFalse(validator.validarFechaCaducidad(fechaCaducidad))
    }

    @Test
    fun tarjetaNula() {
        val tarjeta = Tarjeta("", "")
        assertThrows(TarjetaException.TarjetaNumeroInvalido::class.java) {
            validator.validarTarjeta(tarjeta)
        }
    }

    @Test
    fun numeroCorrectoFechaIncorrecta() {
        val numeroTarjeta = "4532 7233 6544 2231"
        val fechaCaducidad = "1211/212"
        assertThrows(TarjetaException.FechaCaducidadInvalida::class.java) {
            validator.validarTarjeta(Tarjeta(numeroTarjeta, fechaCaducidad))
        }
    }

    @Test
    fun fechaInferiorActual() {
        val numeroTarjeta = "4532 7233 6544 2231"
        val fechaCaducidad = "01/01"
        assertThrows(TarjetaException.FechaCaducidadInvalida::class.java) {
            validator.validarTarjeta(Tarjeta(numeroTarjeta, fechaCaducidad))
        }
    }
}
