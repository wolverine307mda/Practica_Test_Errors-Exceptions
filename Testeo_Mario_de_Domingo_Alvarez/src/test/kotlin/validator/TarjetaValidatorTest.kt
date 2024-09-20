package validator

import error.ClienteError
import error.TarjetaError
import org.example.models.Tarjeta
import org.example.validator.TarjetaValidator
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

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
        assertThrows(TarjetaError.TarjetaNumeroInvalido::class.java) {
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
        assertThrows(TarjetaError.TarjetaNumeroInvalido::class.java) {
            validator.validarTarjeta(tarjeta)
        }
    }

    @Test
    fun numeroCorrectoFechaIncorrecta() {
        val numeroTarjeta = "4532 7233 6544 2231"
        val fechaCaducidad = "1211/212"
        assertThrows(TarjetaError.FechaCaducidadInvalida::class.java) {
            validator.validarTarjeta(Tarjeta(numeroTarjeta,fechaCaducidad))
        }
    }

    @Test
    fun fechaInferiorActual(){
        val numeroTarjeta = "4532 7233 6544 2231"
        val fechaCaducidad = "01/01"
        assertThrows(TarjetaError.FechaCaducidadInvalida::class.java) {
            validator.validarTarjeta(Tarjeta(numeroTarjeta,fechaCaducidad))
        }
    }

    @Test
    fun validarNumeroTarjetaConEspacios() {
        val numeroTarjeta = "4532 7233 6544 2231"  // Formato válido con espacios
        assertTrue(validator.validarNumeroTarjeta(numeroTarjeta))
    }

    @Test
    fun validarNumeroTarjetaSinEspacios() {
        val numeroTarjeta = "4532723365442231"  // Formato válido sin espacios
        assertTrue(validator.validarNumeroTarjeta(numeroTarjeta))
    }

    @Test
    fun numeroTarjetaConCaracteresNoNumericos() {
        val numeroTarjeta = "4532 7233 6544 ABCD"  // Número de tarjeta con caracteres no válidos
        assertFalse(validator.validarNumeroTarjeta(numeroTarjeta))
    }

    @Test
    fun fechaCaducidadConFormatoIncorrecto() {
        val fechaCaducidad = "2025/12"  // Formato incorrecto para la fecha
        assertFalse(validator.validarFechaCaducidad(fechaCaducidad))
    }

    @Test
    fun fechaCaducidadConMesInvalido() {
        val fechaCaducidad = "13/25"  // Mes inválido
        assertFalse(validator.validarFechaCaducidad(fechaCaducidad))
    }

    @Test
    fun validarTarjetaConNumeroValidoYFechaInvalida() {
        val tarjeta = Tarjeta("4532 7233 6544 2231", "00/25")  // Mes cero no es válido
        assertThrows(TarjetaError.FechaCaducidadInvalida::class.java) {
            validator.validarTarjeta(tarjeta)
        }
    }

    @Test
    fun validarTarjetaConNumeroValidoYFechaCaducidadEnFormatoIncorrecto() {
        val tarjeta = Tarjeta("4532 7233 6544 2231", "1234")  // Formato inválido
        assertThrows(TarjetaError.FechaCaducidadInvalida::class.java) {
            validator.validarTarjeta(tarjeta)
        }
    }

    @Test
    fun validarNumeroTarjetaConLongitudIncorrecta() {
        val numeroTarjeta = "4532 7233 6544"  // Número de tarjeta corto
        assertFalse(validator.validarNumeroTarjeta(numeroTarjeta))
    }

    @Test
    fun validarTarjetaConDatosIncorrectos() {
        val tarjeta = Tarjeta("1234 5678 9101 1121", "12/24")  // Número inválido
        assertThrows(TarjetaError.TarjetaNumeroInvalido::class.java) {
            validator.validarTarjeta(tarjeta)
        }
    }

    @Test
    fun validarTarjetaConNumeroValidoYFechaCaducidadPasada() {
        val tarjeta = Tarjeta("4532 7233 6544 2231", "01/20")  // Fecha caducada
        assertThrows(TarjetaError.FechaCaducidadInvalida::class.java) {
            validator.validarTarjeta(tarjeta)
        }
    }


}